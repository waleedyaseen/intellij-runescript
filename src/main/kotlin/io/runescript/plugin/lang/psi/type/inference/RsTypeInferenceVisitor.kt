package io.runescript.plugin.lang.psi.type.inference

import com.intellij.psi.PsiElement
import com.intellij.psi.util.findParentOfType
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.refs.RsDynamicExpressionReference
import io.runescript.plugin.lang.psi.type.*
import io.runescript.plugin.lang.psi.type.trigger.RsTriggerType
import io.runescript.plugin.oplang.psi.RsOpCommand
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
            val project = subjectExpression.project
            var subjectType = triggerType.subjectType
            var subjectName = subjectExpression.text
            if (subjectName != "_") {
                if (subjectName[0] == '_') {
                    subjectName = subjectName.substring(1)
                    subjectType = RsPrimitiveType.CATEGORY
                }
                val reference = RsSymbolIndex.lookup(project, subjectType, subjectName)
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
        o.statementList.accept(this)
    }

    override fun visitParameter(o: RsParameter) {
        o.localVariableExpression.type = findParameterType(o)
    }

    override fun visitBlockStatement(o: RsBlockStatement) {
        o.statementList.accept(this)
    }

    override fun visitStatementList(o: RsStatementList) {
        o.statementList.forEach { it.accept(this) }
    }

    override fun visitIfStatement(o: RsIfStatement) {
        o.expression.typeHint = RsPrimitiveType.BOOLEAN
        o.expression.accept(this)
        checkTypeMismatch(o.expression, RsPrimitiveType.BOOLEAN)

        o.trueStatement.accept(this)
        o.falseStatement?.accept(this)
    }

    override fun visitTypeName(o: RsTypeName) {
        o.type = RsTypeType
    }

    internal fun checkTypeMismatch(context: PsiElement, expectedType: RsType) {
        checkTypeMismatch(context, context.type, expectedType)
    }

    private fun RsType?.unfold(): RsType? {
        if (this is RsTupleType && types.size == 1) return types[0]
        return this
    }

    private fun checkTypeMismatch(context: PsiElement, actualType: RsType?, expectedType: RsType?) {
        val unfoldedActualType = actualType.unfold() ?: RsErrorType
        val unfoldedExpectedType = expectedType.unfold() ?: RsErrorType
        if (unfoldedActualType is RsErrorType || unfoldedExpectedType is RsErrorType) {
            return
        }
        if (unfoldedExpectedType == RsPrimitiveType.OBJ && unfoldedActualType == RsPrimitiveType.NAMEDOBJ) {
            // namedobj extends obj
            return
        }
        if (unfoldedExpectedType == RsPrimitiveType.FONTMETRICS && unfoldedActualType == RsPrimitiveType.GRAPHIC) {
            // graphic extends fontmetrics
            return
        }
        if (unfoldedActualType != unfoldedExpectedType) {
            context.error(
                "Type mismatch: '%s' was given but '%s' was expected".format(
                    unfoldedActualType.representation,
                    unfoldedExpectedType.representation
                )
            )
        }
    }

    override fun visitGosubExpression(o: RsGosubExpression) {
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
            // TODO(Walied): This is off, it doesnt' check if the argument list is not present
            o.argumentList?.let {
                checkArgumentList(it, parameterTypes)
            }
            val returnTypes = reference
                .returnList
                ?.typeNameList
                ?.map { RsPrimitiveType.lookup(it.text) }
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

    override fun visitCommandExpression(o: RsCommandExpression) {
        val reference = o.reference?.resolve()
        if (reference == null) {
            o.type = RsErrorType
        } else {
            reference as RsOpCommand
            with(reference.findCommandHandler()) {
                inferTypes(reference, o)
            }
        }
    }

    fun checkExpressionList(expressionList: List<RsExpression>, parameterTypes: Array<RsType>) {
        var index = 0
        expressionList.forEach {
            if (index < parameterTypes.size) {
                it.typeHint = parameterTypes[index]
            }
            if (it.type == null) {
                it.accept(this)
            }
            // TODO(Walied): Better error reporting here
            val actualType = it.type
            if (actualType != null) {
                val expectedTuple = (0 until actualType.size).map {
                    val expectedIndex = index + it
                    if (expectedIndex < parameterTypes.size) parameterTypes[expectedIndex] else RsErrorType
                }.joined()
                checkTypeMismatch(it, actualType, expectedTuple)
                index += expectedTuple.size
            } else {
                index++
            }
        }
    }

    fun checkArgumentList(argumentList: RsArgumentList, parameterTypes: Array<RsType>) {
        checkExpressionList(argumentList.expressionList, parameterTypes)
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
            o.right.typeHint = o.right.typeHint ?: o.left.type
            o.right.accept(this)
            checkTypeMismatch(o.left, valueRequiredType)
            checkTypeMismatch(o.right, valueRequiredType)
        } else {
            o.right.typeHint = o.left.type
            o.right.accept(this)

            checkTypeMismatch(o.right, o.left.type ?: RsErrorType)
        }
        o.type = RsPrimitiveType.BOOLEAN
    }

    override fun visitWhileStatement(o: RsWhileStatement) {
        o.expression.typeHint = RsPrimitiveType.BOOLEAN
        o.expression.accept(this)
        checkTypeMismatch(o.expression, RsPrimitiveType.BOOLEAN)

        o.statement.accept(this)
    }

    override fun visitArrayVariableDeclarationStatement(o: RsArrayVariableDeclarationStatement) {
        val type = RsPrimitiveType.lookup(o.defineType.text.substring("def_".length))
        val expression = o.expressionList[0]
        expression.type = RsArrayType(type)

        val countExpression = o.expressionList[1]
        countExpression.typeHint = RsPrimitiveType.INT
        countExpression.accept(this)
        checkTypeMismatch(countExpression, RsPrimitiveType.INT)
    }


    override fun visitLocalVariableDeclarationStatement(o: RsLocalVariableDeclarationStatement) {
        val type = RsPrimitiveType.lookup(o.defineType.text.substring("def_".length))
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
        val expectedTypes = variables.map { it.type }
        var index = 0
        for (expr in expressions) {
            if (index < expectedTypes.size) {
                expr.typeHint = expectedTypes[index]
            }
            expr.accept(this)
            val expectedType = expr.type
            if (expectedType != null) {
                checkTypeMismatch(expr, expectedType)
                index += expectedType.size
            } else {
                index++
            }
        }
    }


    override fun visitExpressionStatement(o: RsExpressionStatement) {
        o.expression.accept(this)
    }

    override fun visitSwitchStatement(o: RsSwitchStatement) {
        val type = RsPrimitiveType.lookup(o.switch.text.substring("switch_".length))
        o.typeHint = type
        o.expression.accept(this)
        checkTypeMismatch(o.expression, type)

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
        o.type = RsPrimitiveType.INT

        o.expression.typeHint = RsPrimitiveType.INT
        o.expression.accept(this)
    }

    override fun visitParExpression(o: RsParExpression) {
        o.expression.typeHint = o.typeHint
        o.expression.accept(this)

        o.type = o.expression.type
    }

    override fun visitDynamicExpression(o: RsDynamicExpression) {
        val type = o.typeHint.unfold() ?: RsErrorType
        if (type is RsTypeType) {
            val primitiveType = RsPrimitiveType.lookupOrNull(o.text)
            if (primitiveType == null) {
                o.type = RsErrorType
            } else {
                o.type = RsTypeType
            }
            return
        }
        when (val reference = RsDynamicExpressionReference.resolveElement(o, type).singleOrNull()?.element) {
            null -> o.type = RsErrorType
            is RsOpCommand -> {
                val returnTypes = reference
                    .returnList
                    .typeNameList
                    .map { RsPrimitiveType.lookup(it.text) }
                o.type = RsTupleType(returnTypes.flatten())
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
        o.left.typeHint = RsPrimitiveType.INT
        o.left.accept(this)

        o.right.typeHint = RsPrimitiveType.INT
        o.right.accept(this)
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
        o.type = o.typeHint
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
        // TODO(Walied): Implement when constants have references
        o.type = o.typeHint
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
        if (o.arrayTypeLiteral != null) {
            val definitionLiteral = o.arrayTypeLiteral!!.text
            val typeLiteral = definitionLiteral.substring(0, definitionLiteral.length - "array".length)
            val elementType = RsPrimitiveType.lookup(typeLiteral)
            return RsArrayType(elementType)
        } else if (o.typeName != null) {
            val typeLiteral = o.typeName!!.text
            return RsPrimitiveType.lookup(typeLiteral)
        }
        return RsErrorType
    }

    override fun visitReturnStatement(o: RsReturnStatement) {
        val script = o.findParentOfType<RsScript>(true)!!
        val expectedReturnList = script.returnList
            ?.typeNameList
            ?.map { RsPrimitiveType.lookup(it.text) }
            ?.toTypedArray<RsType>()
        checkExpressionList(o.expressionList, expectedReturnList ?: emptyArray<RsType>())
    }
}

private fun RsOpCommand.findCommandHandler(): CommandHandler {
    return when (commandHeader.nameLiteralList[1].text) {
        "enum" -> EnumCommandHandler
        "struct_param" -> ParamCommandHandler.STRUCT_PARAM
        "lc_param" -> ParamCommandHandler.LC_PARAM
        "nc_param" -> ParamCommandHandler.NC_PARAM
        "oc_param" -> ParamCommandHandler.OC_PARAM
        "db_find" -> DbFindCommandHandler.DB_FIND
        "db_find_with_count" -> DbFindCommandHandler.DB_FIND_WITH_COUNT
        "db_find_refine" -> DbFindCommandHandler.DB_FIND_REFINE
        "db_find_refine_with_count" -> DbFindCommandHandler.DB_FIND_REFINE_WITH_COUNT
        "db_getfield" -> DbGetFieldCommandHandler
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