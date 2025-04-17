package io.runescript.plugin.lang.psi.type.inference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.startOffset
import com.intellij.util.SmartList
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.refs.RsDynamicExpressionReference
import io.runescript.plugin.lang.psi.type.*
import io.runescript.plugin.lang.psi.type.trigger.RsTriggerType
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class RsTypeInferenceVisitor(private val myInferenceData: RsTypeInference) : RsVisitor() {

    override fun visitScript(o: RsScript) {
        val triggerName = o.triggerName
        val triggerType = RsTriggerType.lookup(triggerName)
        if (triggerType == null) {
            o.triggerNameExpression.error(RsBundle.message("inspection.error.unrecognized.trigger.type", triggerName))
            return
        }
        val returnList = o.returnList
        if (returnList != null && !triggerType.allowReturns && returnList.typeNameList.isNotEmpty()) {
            returnList.error(RsBundle.message("inspection.error.trigger.return.list.not.allowed", triggerName))
        }
        val parameterList = o.parameterList
        if (parameterList != null && !triggerType.allowParameters && parameterList.parameterList.isNotEmpty()) {
            parameterList.error(RsBundle.message("inspection.error.trigger.parameter.list.not.allowed", triggerName))
        }
        val subjectExpression = o.scriptNameExpression
        if (triggerType.subjectType != null) {
            var subjectType = triggerType.subjectType
            var subjectName = subjectExpression.text
            if (subjectName != "_") {
                if (subjectName[0] == '_') {
                    subjectName = subjectName.substring(1)
                    subjectType = RsPrimitiveType.CATEGORY
                }
                val reference = RsSymbolIndex.lookup(subjectExpression, subjectType, subjectName)
                if (reference == null) {
                    subjectExpression.error(
                        RsBundle.message(
                            "inspection.error.trigger.subject.not.found",
                            subjectName,
                            subjectType.literal
                        )
                    )
                }
            }
        }
        o.parameterList?.parameterList?.forEach { it.accept(this) }
        val expectedParameterTypes = triggerType.parameterTypes
        if (expectedParameterTypes != null) {
            val actualParametersType = o.parameterList?.parameterList
                ?.map { it.localVariableExpression?.type ?: RsErrorType }
                .fold()
            val expectedParametersType = expectedParameterTypes.toList().fold()
            checkTypeMismatch(o.scriptNameExpression, actualParametersType, expectedParametersType)
        }
        if (o.statementList.statementList.size > 0 && triggerType == RsTriggerType.COMMAND) {
            o.statementList.error(RsBundle.message("inspection.error.trigger.code.not.allowed"))
        }
        o.statementList.accept(this)
    }

    override fun visitParameter(o: RsParameter) {
        o.localVariableExpression?.type = findParameterType(o)
    }

    override fun visitBlockStatement(o: RsBlockStatement) {
        o.statementList.accept(this)
    }

    override fun visitStatementList(o: RsStatementList) {
        o.statementList.forEach { it.accept(this) }
    }

    override fun visitIfStatement(o: RsIfStatement) {
        val expression = o.expression
        if (expression != null) {
            expression.typeHint = RsPrimitiveType.BOOLEAN
            expression.accept(this)
            checkTypeMismatch(expression, RsPrimitiveType.BOOLEAN)
        }
        o.trueStatement?.accept(this)
        o.falseStatement?.accept(this)
    }

    override fun visitTypeName(o: RsTypeName) {
        o.type = RsTypeType
    }

    private fun checkTypeMismatch(context: PsiElement, expectedType: RsType) {
        checkTypeMismatch(context, context.type, expectedType)
    }

    private fun RsType?.unfold(): RsType? {
        if (this is RsTupleType && types.size == 1) {
            return types[0]
        }
        return this
    }

    private fun RsType?.flatten(): SmartList<RsType> {
        if (this == null) {
            return SmartList()
        }
        if (this is RsTupleType) {
            val result = SmartList<RsType>()
            types.forEach {
                result.addAll(it.flatten())
            }
            return result
        }
        return SmartList(this)
    }

    private fun Collection<RsType>?.fold(): RsType {
        if (this == null) {
            return RsUnitType
        }
        if (size == 1) {
            return first()
        }
        return RsTupleType(flatten())
    }

    private fun checkTypeMismatch(
        context: PsiElement,
        actualType: RsType?,
        expectedType: RsType?,
        reportError: Boolean = true
    ): Boolean {
        val unfoldedActualType = actualType.unfold() ?: RsErrorType
        val unfoldedExpectedType = expectedType.unfold() ?: RsErrorType
        if (unfoldedActualType is RsErrorType || unfoldedExpectedType is RsErrorType) {
            return false
        }
        if (unfoldedActualType is RsTupleType && RsErrorType in unfoldedActualType.types) {
            return false
        }
        if (unfoldedExpectedType is RsTupleType && RsErrorType in unfoldedExpectedType.types) {
            return false
        }
        val flattenedActualType = unfoldedActualType.flatten()
        val flattenedExpectedType = unfoldedExpectedType.flatten()
        if (flattenedActualType.size != flattenedExpectedType.size
            || !flattenedActualType.zip(flattenedExpectedType).all { isSingleTypeMatch(it.second, it.first) }
        ) {
            if (reportError) {
                context.error(
                    TYPE_MISMATCH_ERROR.format(
                        unfoldedActualType.representation,
                        unfoldedExpectedType.representation
                    )
                )
            }
            return false
        }
        return true
    }

    private fun isSingleTypeMatch(unfoldedExpectedType: RsType, unfoldedActualType: RsType): Boolean {
        check(unfoldedActualType !is RsTupleType && unfoldedExpectedType !is RsTupleType)
        if (unfoldedExpectedType == RsPrimitiveType.OBJ && unfoldedActualType == RsPrimitiveType.NAMEDOBJ) {
            // namedobj extends obj
            return true
        }
        if (unfoldedExpectedType == RsPrimitiveType.FONTMETRICS && unfoldedActualType == RsPrimitiveType.GRAPHIC) {
            // graphic extends fontmetrics
            return true
        }
        if (unfoldedExpectedType.isHookType() && unfoldedActualType == RsPrimitiveType.STRING) {
            // hooks are just strings that are parsed different.
            return true
        }
        return unfoldedActualType == unfoldedExpectedType
    }

    override fun visitGosubExpression(o: RsGosubExpression) {
        val reference = o.reference?.resolve()
        if (reference == null) {
            o.type = RsErrorType
        } else {
            reference as RsScript
            val expectedTypes = reference
                .parameterList
                ?.parameterList
                ?.map { findParameterType(it) }
                ?.flatten()
                ?: emptyArray()
            val actual = o.argumentList?.expressionList ?: emptyList()
            // TODO(Waleed): Thre is a case where arguments list length does not match the expected length
            //  causing an RsErrorType to be produced in actual types array preventing error reporting.
            checkExpressionList(o.argumentList ?: o, actual, expectedTypes)
            val returnTypes = reference
                .returnList
                ?.typeNameList
                ?.map { RsPrimitiveType.lookupReferencable(it.text) }
                ?: emptyList()

            if (returnTypes.isEmpty()) {
                o.type = RsUnitType
            } else if (returnTypes.size == 1) {
                o.type = returnTypes[0]
            } else {
                o.type = RsTupleType(returnTypes.flatten())
            }
        }
    }

    override fun visitHookRoot(o: RsHookRoot) {
        o.hookFragment.accept(this)
    }

    override fun visitHookFragment(o: RsHookFragment) {
        val host = InjectedLanguageManager.getInstance(o.project).getInjectionHost(o)!!
        val hostInferenceData = host.parentOfType<RsInferenceDataHolder>(true)!!.inferenceData
        val typeHint = hostInferenceData.typeHintOf(host)
        val reference = o.reference?.resolve()
        if (reference == null) {
            o.type = RsErrorType
        } else {
            reference as RsScript
            val parameterTypes = reference
                .parameterList
                ?.parameterList
                ?.map { findParameterType(it) }
                ?.flatten()
                ?: emptyArray()
            o.argumentList?.let { checkArgumentList(it, parameterTypes) }
            val expectedType = when (typeHint) {
                RsPrimitiveType.VARPHOOK -> RsPrimitiveType.VARP
                RsPrimitiveType.VARCHOOK -> RsPrimitiveType.VARC
                RsPrimitiveType.STATHOOK -> RsPrimitiveType.STAT
                RsPrimitiveType.INVHOOK -> RsPrimitiveType.INV
                else -> null
            }
            val hookTransmitList = o.hookTransmitList
            if (hookTransmitList != null) {
                if (expectedType == null) {
                    hookTransmitList.error("${reference.name} does not allow transmit list.")
                }
                hookTransmitList.expressionList.forEach {
                    it.typeHint = expectedType ?: RsErrorType
                    it.accept(this)
                }
            }
        }
    }

    override fun visitCommandExpression(o: RsCommandExpression) {
        val reference = o.reference?.resolve()
        if (reference == null) {
            o.type = RsErrorType
        } else {
            reference as RsScript
            with(reference.findCommandHandler()) {
                inferTypes(reference, o)
            }
        }
    }

    private fun checkExpressionList(
        context: PsiElement,
        expressionList: List<RsExpression>,
        parameterTypes: Array<RsType>
    ) {
        var index = 0
        val actualTypes = expressionList.map {
            if (index < parameterTypes.size) {
                it.typeHint = parameterTypes[index]
            }
            if (it.type == null) {
                it.accept(this)
            }
            val actualType = it.type
            if (actualType != null) {
                index += actualType.size
                actualType
            } else {
                index++
                RsErrorType
            }
        }.joined()
        checkTypeMismatch(context, actualTypes, parameterTypes.toList().joined())
    }

    fun checkArgumentList(argumentList: RsArgumentList, parameterTypes: Array<RsType>) {
        checkExpressionList(argumentList, argumentList.expressionList, parameterTypes)
    }

    override fun visitConditionExpression(o: RsConditionExpression) {
        val op = o.conditionOp
        val valueRequiredType = when {
            op.isLogicalAnd() || op.isLogicalOr() -> RsPrimitiveType.BOOLEAN
            op.isRelational() -> RsPrimitiveType.INT
            else -> null
        }
        if (valueRequiredType != null) {
            o.left.typeHint = valueRequiredType
            o.right.typeHint = valueRequiredType
        } else {
            o.left.typeHint = o.right.type
            o.right.typeHint = o.left.type
        }
        o.left.accept(this)

        if (valueRequiredType != null) {
            o.right.typeHint = o.left.type ?: o.right.typeHint
            o.right.accept(this)
            if (op.isRelational()) {
                var expectedType = o.left.type ?: o.right.type
                if (expectedType != RsPrimitiveType.LONG) {
                    expectedType = valueRequiredType
                }
                checkTypeMismatch(o.left, expectedType)
                checkTypeMismatch(o.right, expectedType)
            } else {
                checkTypeMismatch(o.left, valueRequiredType)
                checkTypeMismatch(o.right, valueRequiredType)
            }
        } else {
            o.right.typeHint = o.left.type
            o.right.accept(this)

            checkTypeMismatch(o.right, o.left.type ?: RsErrorType)
        }
        o.type = RsPrimitiveType.BOOLEAN
    }

    override fun visitWhileStatement(o: RsWhileStatement) {
        val expression = o.expression
        if (expression != null) {
            expression.typeHint = RsPrimitiveType.BOOLEAN
            expression.accept(this)
            checkTypeMismatch(expression, RsPrimitiveType.BOOLEAN)
        }
        o.statement?.accept(this)
    }

    override fun visitArrayVariableDeclarationStatement(o: RsArrayVariableDeclarationStatement) {
        val type = RsPrimitiveType.lookupReferencable(o.defineType.text.substring("def_".length))
        val expression = o.expressionList[0]
        expression.type = RsArrayType(type)
        if (o.expressionList.size > 1) {
            val countExpression = o.expressionList[1]
            countExpression.typeHint = RsPrimitiveType.INT
            countExpression.accept(this)
            checkTypeMismatch(countExpression, RsPrimitiveType.INT)
        }
    }


    override fun visitLocalVariableDeclarationStatement(o: RsLocalVariableDeclarationStatement) {
        val type = RsPrimitiveType.lookupReferencable(o.defineType.text.substring("def_".length))
        if (o.expressionList.isEmpty()) {
            return
        }
        val expression = o.expressionList[0]
        expression.type = type
        // expression.accept(this)
        if (o.expressionList.size > 1) {
            val assignment = o.expressionList[1]
            assignment.typeHint = type
            assignment.accept(this)
            checkTypeMismatch(assignment, type)
        }
    }

    override fun visitScopedVariableExpression(o: RsScopedVariableExpression) {
        val reference = o.reference?.resolve() as? RsSymSymbol
        if (reference == null) {
            o.type = RsErrorType
            return
        }
        val isVarbit = reference.containingFile.virtualFile.nameWithoutExtension == "varbit"
        if (isVarbit) {
            o.type = RsPrimitiveType.INT
        } else {
            if (reference.fieldList.size < 3) {
                o.error("Missing type field for game variable symbol")
                o.type = RsErrorType
                return
            }
            o.type = RsPrimitiveType.lookupReferencableOrNull(reference.fieldList[2].text) ?: RsErrorType
        }
    }

    override fun visitLocalVariableExpression(o: RsLocalVariableExpression) {
        val resolvedExpression = o.reference?.resolve()
        o.type = resolvedExpression?.type ?: RsErrorType
    }

    override fun visitAssignmentStatement(o: RsAssignmentStatement) {
        val variables = mutableListOf<RsExpression>()
        val expressions = mutableListOf<RsExpression>()
        val equalOffset = o.equal.startOffset
        for (expr in o.expressionList) {
            if (expr.startOffset < equalOffset) {
                variables.add(expr)
            } else {
                expressions.add(expr)
            }
        }
        variables.forEach { it.accept(this) }
        val expectedTypes = variables.map { it.type ?: RsErrorType }
        var index = 0
        for (expr in expressions) {
            if (index < expectedTypes.size) {
                expr.typeHint = expectedTypes[index]
            }
            expr.accept(this)
            val expectedType = expr.type
            if (expectedType != null) {
                index += expectedType.size
            } else {
                index++
            }
        }
        val actualTypes = expressions.map { it.type ?: RsErrorType }.joined()
        checkTypeMismatch(o, actualTypes, expectedTypes.joined())
    }


    override fun visitExpressionStatement(o: RsExpressionStatement) {
        o.expression.accept(this)
    }

    override fun visitEmptyStatement(o: RsEmptyStatement) {
        // Do nothing.
    }

    override fun visitSwitchStatement(o: RsSwitchStatement) {
        val type = RsPrimitiveType.lookupReferencable(o.switch.text.substring("switch_".length))
        val expression = o.expression
        if (expression != null) {
            o.typeHint = type
            expression.accept(this)
            checkTypeMismatch(expression, type)
        }
        o.switchCaseList.forEach {
            it.expressionList.forEach { expression ->
                expression.typeHint = type
            }
            it.accept(this)
        }
    }

    override fun visitSwitchCase(o: RsSwitchCase) {
        o.expressionList.forEach { it.accept(this) }
        o.statementList.accept(this)
    }

    override fun visitCalcExpression(o: RsCalcExpression) {
        o.type = o.typeHint ?: RsPrimitiveType.INT

        o.expression.typeHint = o.typeHint ?: RsPrimitiveType.INT
        o.expression.accept(this)
    }

    override fun visitParExpression(o: RsParExpression) {
        o.expression.typeHint = o.typeHint
        o.expression.accept(this)

        o.type = o.expression.type
    }

    override fun visitPrefixExpression(o: RsPrefixExpression) {
        visitUnaryExpression(o)
    }

    override fun visitPostfixExpression(o: RsPostfixExpression) {
        visitUnaryExpression(o)
    }

    private fun visitUnaryExpression(o: RsUnaryExpression) {
        o.expression.typeHint = o.typeHint
        o.expression.accept(this)
        val type = o.expression.type
        if (type == null || type is RsErrorType) {
            o.type = RsErrorType
            return
        }
        if (type == RsPrimitiveType.INT || type == RsPrimitiveType.LONG) {
            o.type = type
        } else {
            o.error(INVALID_UNARY_OPERATOR_ERROR.format(o.unaryOp.text, type.representation))
            o.type = RsErrorType
        }
    }

    override fun visitDynamicExpression(o: RsDynamicExpression) {
        val type = o.typeHint.unfold() ?: RsErrorType
        if (type is RsTypeType) {
            val primitiveType = RsPrimitiveType.lookupReferencableOrNull(o.text)
            if (primitiveType == null) {
                o.type = RsErrorType
            } else {
                o.type = RsTypeType
            }
            return
        }
        when (val reference = RsDynamicExpressionReference.resolveElement(o, type).singleOrNull()?.element) {
            null -> o.type = RsErrorType
            is RsScript -> {
                if (reference.triggerName == "command") {
                    val returnTypes = reference
                        .returnList
                        ?.typeNameList
                        ?.map { RsPrimitiveType.lookupReferencable(it.text) }
                        ?: emptyList()
                    o.type = RsTupleType(returnTypes.flatten())
                } else {
                    check(type == RsPrimitiveType.CLIENTOPNPC || type == RsPrimitiveType.CLIENTOPLOC
                            || type == RsPrimitiveType.CLIENTOPOBJ || type == RsPrimitiveType.CLIENTOPPLAYER
                            || type == RsPrimitiveType.CLIENTOPTILE) {
                        "Invalid type for dynamic expression: ${type.representation}"
                    }
                    o.type = type
                }
            }

            is RsSymSymbol -> {
                o.type = type
            }

            else -> {
                o.type = reference.type
            }
        }
    }

    override fun visitArgumentList(o: RsArgumentList) {
        o.expressionList.forEach { it.accept(this) }
    }

    override fun visitArrayAccessExpression(o: RsArrayAccessExpression) {
        // access
        o.expressionList[1].typeHint = RsPrimitiveType.INT
        o.expressionList[1].accept(this)

        // variable
        o.expressionList[0].accept(this)
        val arrayType = o.expressionList[0].type!!
        if (arrayType !is RsArrayType) {
            o.expressionList[0].error("Type mismatch: '%s' was given but array was expected".format(arrayType.representation))
            o.type = RsErrorType
            return
        }
        o.type = arrayType.elementType
        o.expressionList[0].type = arrayType.elementType
    }

    override fun visitArithmeticExpression(o: RsArithmeticExpression) {
        o.left.typeHint = o.typeHint ?: RsPrimitiveType.INT
        o.left.accept(this)

        o.right.typeHint = o.typeHint ?: RsPrimitiveType.INT
        o.right.accept(this)

        val leftType = o.left.type.unfold()
        val rightType = o.right.type.unfold()

        if (leftType == null || rightType == null || leftType is RsErrorType || rightType == RsErrorType) return

        val operator = o.arithmeticOp

        if (leftType != rightType) {
            o.error(INVALID_OPERATOR_ERROR.format(operator.text, leftType.representation, rightType.representation))
        }
    }

    override fun visitArithmeticValueExpression(o: RsArithmeticValueExpression) {
        o.expression?.accept(this)
    }

    override fun visitRelationalValueExpression(o: RsRelationalValueExpression) {
        o.expression?.accept(this)
    }

    override fun visitStringLiteralExpression(o: RsStringLiteralExpression) {
        o.stringLiteralContent.typeHint = o.typeHint
        o.stringLiteralContent.accept(this)
        o.type = o.typeHint ?: RsPrimitiveType.STRING
    }

    override fun visitStringLiteralContent(o: RsStringLiteralContent) {
        if (o.typeHint != RsPrimitiveType.STRING && !o.isBasicContent()) {
            o.error("Only basic strings are allowed as quoted identifiers")
        }
        o.stringInterpolationExpressionList.forEach {
            it.accept(this)
        }
    }

    override fun visitStringInterpolationExpression(o: RsStringInterpolationExpression) {
        o.typeHint = RsPrimitiveType.STRING
        o.expression.accept(this)
        checkTypeMismatch(o.expression, RsPrimitiveType.STRING)
    }

    override fun visitConstantExpression(o: RsConstantExpression) {
        val reference = o.reference?.resolve() as? RsSymSymbol
        val type = o.typeHint
        if (reference == null || type !is RsPrimitiveType || !type.referencable) {
            o.type = RsErrorType
            return
        }
        o.type = o.typeHint
        val value = reference.fieldList[1].text
        when (type) {
            RsPrimitiveType.STRING -> {
                // Do nothing
            }

            RsPrimitiveType.INT -> {
                val radix: Int
                val trimmedValue: String
                if (value.startsWith("0x")) {
                    radix = 16
                    trimmedValue = value.substring(2, value.length)
                } else {
                    radix = 10
                    trimmedValue = value.substring(0, value.length)
                }
                if (trimmedValue.toIntOrNull(radix) == null) {
                    o.error("Could not convert constant value '${value}' to an integer number.")
                }
            }

            RsPrimitiveType.LONG -> {
                val radix: Int
                val trimmedValue: String
                val endIndex = if (value.endsWith('L', ignoreCase = true)) value.length - 1 else value.length
                if (value.startsWith("0x")) {
                    radix = 16
                    trimmedValue = value.substring(2, endIndex)
                } else {
                    radix = 10
                    trimmedValue = value.substring(0, endIndex)
                }
                if (trimmedValue.toLongOrNull(radix) == null) {
                    o.error("Could not convert constant value '${value}' to a long number.")
                }
            }

            else -> {
                val configReference = RsSymbolIndex.lookup(o, type, value)
                if (configReference == null) {
                    o.error("Could not resolve constant value '${value} to a reference.'")
                }
            }
        }
    }

    override fun visitNullLiteralExpression(o: RsNullLiteralExpression) {
        // TOOD(Walied): Shouldn't need the unfodl here
        val typeHint = o.typeHint.unfold()
        if (typeHint.isNullOrError()) {
            o.type = RsErrorType
            return
        }
        if (typeHint !is RsPrimitiveType || (typeHint != RsPrimitiveType.STRING && typeHint.baseType != RsBaseType.INT && typeHint.baseType != RsBaseType.LONG)) {
            // TODO(Walied): We allow strings for now to bypass hooks error in cc_setontimer(null)
            o.error("Cannot convert <null> to ${typeHint.representation}")
            return
        }
        o.type = typeHint
    }

    override fun visitIntegerLiteralExpression(o: RsIntegerLiteralExpression) {
        o.type = RsPrimitiveType.INT
    }

    override fun visitLongLiteralExpression(o: RsLongLiteralExpression) {
        o.type = RsPrimitiveType.LONG
    }

    override fun visitCoordLiteralExpression(o: RsCoordLiteralExpression) {
        o.type = RsPrimitiveType.COORD
    }

    override fun visitBooleanLiteralExpression(o: RsBooleanLiteralExpression) {
        o.type = RsPrimitiveType.BOOLEAN
    }

    internal fun PsiElement.error(message: String) {
        myInferenceData.addError(this, message)
    }

    internal var PsiElement.type: RsType?
        get() = myInferenceData.typeOf(this)
        set(value) = myInferenceData.typeInferred(this, value)

    internal var PsiElement.typeHint: RsType?
        get() = myInferenceData.typeHintOf(this)
        set(value) = myInferenceData.typeHintInferred(this, value)

    private fun findParameterType(o: RsParameter): RsType {
        return if (o.arrayTypeLiteral != null) {
            val definitionLiteral = o.arrayTypeLiteral!!.text
            val typeLiteral = definitionLiteral.substring(0, definitionLiteral.length - "array".length)
            val elementType = RsPrimitiveType.lookupReferencable(typeLiteral)
            RsArrayType(elementType)
        } else if (o.typeName != null) {
            val typeLiteral = o.typeName!!.text
            if (o.parentOfType<RsScript>()?.triggerName == "command") {
                RsPrimitiveType.lookup(typeLiteral)
            } else {
                RsPrimitiveType.lookupReferencable(typeLiteral)
            }
        } else {
            RsErrorType
        }
    }

    override fun visitReturnStatement(o: RsReturnStatement) {
        val script = o.findParentOfType<RsScript>(true)!!
        val expectedReturnList = script.returnList
            ?.typeNameList
            ?.map { RsPrimitiveType.lookupReferencable(it.text) }
            ?.toTypedArray<RsType>()
        checkExpressionList(o, o.expressionList, expectedReturnList ?: emptyArray<RsType>())
    }

    companion object {
        private const val TYPE_MISMATCH_ERROR = "Type mismatch: '%s' was given but '%s' was expected"
        private const val INVALID_OPERATOR_ERROR = "Operator '%s' cannot be applied to '%s', '%s'"
        private const val INVALID_UNARY_OPERATOR_ERROR = "Operator '%s' cannot be applied to '%s'"
    }
}

private fun RsScript.findCommandHandler(): CommandHandler {
    return when (nameLiteralList[1].text) {
        "enum" -> EnumCommandHandler
        "enum_getreverseindex" -> EnumGetReverseIndexCommandHandler
        "struct_param" -> ParamCommandHandler.STRUCT_PARAM
        "lc_param" -> ParamCommandHandler.LC_PARAM
        "nc_param" -> ParamCommandHandler.NC_PARAM
        "oc_param" -> ParamCommandHandler.OC_PARAM
        "db_find" -> DbFindCommandHandler.DB_FIND
        "db_find_with_count" -> DbFindCommandHandler.DB_FIND_WITH_COUNT
        "db_find_refine" -> DbFindCommandHandler.DB_FIND_REFINE
        "db_find_refine_with_count" -> DbFindCommandHandler.DB_FIND_REFINE_WITH_COUNT
        "db_getfield" -> DbGetFieldCommandHandler
        "dump" -> DumpCommandHandler
        "cc_create", ".cc_create" -> CcCreateCommandHandler
        else -> DefaultCommandHandler
    }
}

@OptIn(ExperimentalContracts::class)
fun RsType?.isNullOrError(): Boolean {
    contract {
        returns(false) implies (this@isNullOrError != null)
    }
    return this === null || this === RsErrorType
}