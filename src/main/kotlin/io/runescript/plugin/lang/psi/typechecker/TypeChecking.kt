package io.runescript.plugin.lang.psi.typechecker

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.refs.RsDynamicExpressionReference
import io.runescript.plugin.lang.psi.refs.RsIntegerLiteralReference
import io.runescript.plugin.lang.psi.refs.RsStringLiteralReference
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticType
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableSymbol
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.trigger.CommandTrigger
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerManager
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.*
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.GameVarType
import io.runescript.plugin.symbollang.psi.RsSymField
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import org.toml.lang.psi.ext.elementType

class TypeChecking(
    private val triggerManager: TriggerManager,
    private val typeManager: TypeManager,
    private val diagnostics: Diagnostics,
    private val rootTable: LocalVariableTable,
    private val dynamicCommands: Map<String, DynamicCommandHandler>,
    private val symbolLoaders: Map<String, (subTypes: Type) -> Type>,
    private val arraysV2: Boolean,
) : RsVisitor() {
    /**
     * The trigger that represents 'command'.
     */
    private val commandTrigger = triggerManager.find("command")

    /**
     * The trigger that represents `proc`.
     */
    private val procTrigger = triggerManager.find("proc")

    /**
     * The trigger that represents `clientscript`. This trigger is optional.
     */
    private val clientscriptTrigger = triggerManager.findOrNull("clientscript")

    /**
     * The trigger that represents `label`. This trigger is optional.
     */
    private val labelTrigger = triggerManager.findOrNull("label")

    /**
     * The current table. This is updated each time when entering a new script or block.
     */
    private var table: LocalVariableTable = rootTable

    /**
     * Sets the active [table] to [newTable] and runs [block] then sets [table] back to what it was originally.
     */
    private inline fun scoped(newTable: LocalVariableTable, block: () -> Unit) {
        val oldTable = table
        table = newTable
        block()
        table = oldTable
    }

    override fun visitScript(script: RsScript) {
        scoped(script.scope) {
            // visit all statements, we don't need to do anything else with the script
            // since all the other stuff is handled in pre-type checking.
            script.statementList.statementList.visit()
        }
    }

    override fun visitBlockStatement(blockStatement: RsBlockStatement) {
        scoped(blockStatement.scope) {
            // visit all statements
            blockStatement.statementList.statementList.visit()
        }
    }

    override fun visitReturnStatement(returnStatement: RsReturnStatement) {
        val script = returnStatement.parentOfType<RsScript>()
        if (script == null) {
            // a return statement should always be within a script, if not
            // then we have problems!
            returnStatement.reportError(DiagnosticMessage.RETURN_ORPHAN)
            return
        }

        // use the return types from the script node and get the types being returned
        val expectedTypes = TupleType.toList(script.returnType)
        val actualTypes = typeHintExpressionList(expectedTypes, returnStatement.expressionList)

        // convert the types into a single type
        val expectedType = TupleType.fromList(expectedTypes)
        val actualType = TupleType.fromList(actualTypes)

        // type check
        checkTypeMatch(returnStatement, expectedType, actualType)
    }

    override fun visitIfStatement(ifStatement: RsIfStatement) {
        checkCondition(ifStatement.expression)
        ifStatement.trueStatement?.visit()
        ifStatement.falseStatement?.visit()
    }

    override fun visitWhileStatement(whileStatement: RsWhileStatement) {
        checkCondition(whileStatement.expression)
        whileStatement.statement?.visit()
    }

    /**
     * Handles type hinting and visiting the expression, then checking if it is a valid conditional
     * expression. If the condition returns anything other than `boolean`, or is not a valid
     * condition expression, an error is emitted.
     */
    private fun checkCondition(expression: RsExpression?) {
        if (expression == null) {
            // if the expression is null, we can't do anything
            return
        }

        // type hint and visit condition
        expression.typeHint = PrimitiveType.BOOLEAN

        // attempts to find the first expression that isn't a binary expression or parenthesis expression
        val invalidExpression = findInvalidConditionExpression(expression)
        if (invalidExpression == null) {
            // visit expression and type check it, we don't visit outside this because we don't want
            // to report type mismatches AND invalid conditions at the same time.
            expression.visit()
            checkTypeMatch(expression, PrimitiveType.BOOLEAN, expression.type)
        } else {
            // report invalid condition expression on the erroneous node.
            invalidExpression.reportError(DiagnosticMessage.CONDITION_INVALID_NODE_TYPE)
        }
    }

    /**
     * Finds the first [RsExpression] node in the tree that is not either a [RsBinaryExpression] or
     * [RsParExpression]. If `null` is returned then that means the whole tree is valid
     * is all valid conditional expressions.
     */
    private fun findInvalidConditionExpression(expression: RsExpression): PsiElement? = when (expression) {
        is RsConditionExpression -> if (expression.conditionOp.text == "|" || expression.conditionOp.text == "&") {
            // check the left side and return it if it isn't null, otherwise return the value
            // of the right side
            findInvalidConditionExpression(expression.left) ?: findInvalidConditionExpression(expression.right)
        } else {
            // all other operators are valid
            null
        }

        is RsParExpression -> findInvalidConditionExpression(expression.expression)
        is RsRelationalValueExpression -> {
            val expr = expression.expression
            if (expression.lparen != null && expr != null) {
                findInvalidConditionExpression(expr)
            } else {
                expression.expression
            }
        }
        else -> expression
    }

    override fun visitSwitchStatement(switchStatement: RsSwitchStatement) {
        val expectedType = switchStatement.type

        // type hint the condition and visit it
        val condition = switchStatement.expression
        if (condition != null) {
            condition.typeHint = expectedType
            condition.visit()
            checkTypeMatch(condition, expectedType, condition.type)
        }

        // TODO check for duplicate case labels (other than default)
        // visit all the cases, cases will be type checked there.
        var defaultCase: RsSwitchCase? = null
        for (case in switchStatement.switchCaseList) {
            val isDefault = case.children.any {
                it.elementType == RsElementTypes.DEFAULT
            }
            if (isDefault) {
                if (defaultCase == null) {
                    defaultCase = case
                } else {
                    case.reportError(DiagnosticMessage.SWITCH_DUPLICATE_DEFAULT)
                }
            }
            case.visit()
        }
    }

    override fun visitSwitchCase(switchCase: RsSwitchCase) {
        val switchType = switchCase.parentOfType<RsSwitchStatement>()?.type
        if (switchType == null) {
            // the parent should always be a switch statement, if not we're in trouble...
            switchCase.reportError(DiagnosticMessage.CASE_WITHOUT_SWITCH)
            return
        }

        // visit the case keys
        for (key in switchCase.expressionList) {
            // type hint and visit so we can access more information in constant expression check
            key.typeHint = switchType
            key.visit()

            if (!isConstantExpression(key)) {
                key.reportError(DiagnosticMessage.SWITCH_CASE_NOT_CONSTANT)
                continue
            }

            // expression is a constant, now we need to verify the types match
            checkTypeMatch(key, switchType, key.type)
        }

        scoped(switchCase.scope) {
            // visit the statements
            switchCase.statementList.statementList.visit()
        }
    }

    override fun visitSwitchCaseDefaultExpression(o: RsSwitchCaseDefaultExpression) {
        o.type = o.typeHint ?: MetaType.Error
    }

    /**
     * Checks if the result of [expression] is known at compile time.
     */
    internal fun isConstantExpression(expression: RsExpression): Boolean = when (expression) {
        is RsConstantExpression, is RsSwitchCaseDefaultExpression -> true
        is RsStringLiteralExpression -> {
            // we need to special case this since it's possible for a string literal to have been
            // transformed into another expression type (e.g. graphic or clientscript)
//            val sub = expression.subExpression
//            sub == null || isConstantExpression(sub) TODO(Walied):
            val stringContent = expression.stringLiteralContent
            !stringContent.isHookExpression() && stringContent.stringInterpolationExpressionList.isEmpty()
        }

        is RsLiteralExpression -> true
        is RsDynamicExpression -> {
            val ref = RsDynamicExpressionReference.resolveElement(expression, expression.type)
                .firstOrNull()?.element
            ref == null || isConstantSymbol(ref)
        }

        else -> false
    }

    /**
     * Checks if the value of [symbol] is known at compile time.
     */
    private fun isConstantSymbol(symbol: PsiElement?): Boolean = when (symbol) {
        is RsSymSymbol -> true
        else -> false
    }

    override fun visitLocalVariableDeclarationStatement(declarationStatement: RsLocalVariableDeclarationStatement) {
        val typeName = declarationStatement.defineType.text.removePrefix("def_")
        val name = declarationStatement.variable.nameLiteral.text
        val type = typeManager.findOrNull(typeName, allowArray = arraysV2)

        // notify invalid type
        if (type == null) {
            declarationStatement.defineType.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeName)
        } else if (!type.options.allowDeclaration) {
            declarationStatement.defineType.reportError(
                DiagnosticMessage.LOCAL_DECLARATION_INVALID_TYPE,
                type.representation
            )
        }

        // attempt to insert the local variable into the symbol table and display error if failed to insert
        val symbol = LocalVariableSymbol(name, type ?: MetaType.Error)
        val inserted = table.insert(symbol)
        if (!inserted) {
            declarationStatement.variable.reportError(DiagnosticMessage.SCRIPT_LOCAL_REDECLARATION, name)
        }

        declarationStatement.variable.type = symbol.type

        // visit the initializer if it exists to resolve references in it
        val initializer = declarationStatement.initializer
        if (initializer != null) {
            // type hint that we want whatever the declarations type is then visit
            initializer.typeHint = symbol.type
            initializer.visit()

            checkTypeMatch(initializer, symbol.type, initializer.type)
        }

        declarationStatement.variable.type = symbol.type
    }

    override fun visitArrayVariableDeclarationStatement(arrayDeclarationStatement: RsArrayVariableDeclarationStatement) {
        val typeName = arrayDeclarationStatement.defineType.text.removePrefix("def_")
        val name = arrayDeclarationStatement.variable.nameLiteral.text
        var type = typeManager.findOrNull(typeName)

        // notify invalid type
        if (type == null) {
            arrayDeclarationStatement.defineType.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeName)
        } else if (!type.options.allowDeclaration) {
            arrayDeclarationStatement.defineType.reportError(
                DiagnosticMessage.LOCAL_DECLARATION_INVALID_TYPE,
                type.representation,
            )
        } else if (!type.options.allowArray) {
            arrayDeclarationStatement.defineType.reportError(
                DiagnosticMessage.LOCAL_ARRAY_INVALID_TYPE,
                type.representation,
            )
        }

        type = if (type != null) {
            // convert type into an array of type
            ArrayType(type)
        } else {
            // type doesn't exist so give it error type
            MetaType.Error
        }

        arrayDeclarationStatement.variable.type = type

        // visit the initializer if it exists to resolve references in it
        val initializer = arrayDeclarationStatement.initializer
        if (initializer != null) {
            initializer.typeHint = PrimitiveType.INT
            initializer.visit()
            checkTypeMatch(initializer, PrimitiveType.INT, initializer.type)
        }
        // attempt to insert the local variable into the symbol table and display error if failed to insert
        val symbol = LocalVariableSymbol(name, type)
        val inserted = table.insert(symbol)
        if (!inserted) {
            arrayDeclarationStatement.variable.reportError(DiagnosticMessage.SCRIPT_LOCAL_REDECLARATION, name)
        }
    }

    override fun visitConstantExpression(o: RsConstantExpression) {
        o.type = o.typeHint ?: MetaType.Error
    }

    override fun visitHookRoot(o: RsHookRoot) {
        o.hookFragment.visit()
    }

    override fun visitHookFragment(o: RsHookFragment) {
        val ilm = InjectedLanguageManager.getInstance(o.project)
        val host = ilm.getInjectionHost(o)
        check(host is RsStringLiteralContent)

        val parent = host.parent
        check(parent is RsStringLiteralExpression)

        val scope = parent.hookScope ?: return

        scoped(scope) {
            val clientscriptTrigger = checkNotNull(clientscriptTrigger)
            checkCallExpression(o, clientscriptTrigger, DiagnosticMessage.CLIENTSCRIPT_REFERENCE_UNRESOLVED)
        }
    }

    override fun visitAssignmentStatement(assignmentStatement: RsAssignmentStatement) {
        val vars = mutableListOf<RsExpression>()
        val expressions = mutableListOf<RsExpression>()
        val equalOffset = assignmentStatement.equal.startOffset
        for (expr in assignmentStatement.expressionList) {
            if (expr.startOffset < equalOffset) {
                vars.add(expr)
            } else {
                expressions.add(expr)
            }
        }

        // visit the lhs to fetch the references
        vars.visit()

        // store the lhs types to help with type hinting
        val leftTypes = vars.map { it.type }
        val rightTypes = typeHintExpressionList(leftTypes, expressions)

        // convert types to tuple type if necessary for easy comparison
        val leftType = TupleType.fromList(leftTypes)
        val rightType = TupleType.fromList(rightTypes)

        checkTypeMatch(assignmentStatement, leftType, rightType)

        // prevent multi assignment involving arrays
        val firstArrayRef = vars.firstOrNull { it is RsArrayAccessExpression }
        if (vars.size > 1 && firstArrayRef != null) {
            firstArrayRef.reportError(DiagnosticMessage.ASSIGN_MULTI_ARRAY)
        }
    }

    override fun visitExpressionStatement(expressionStatement: RsExpressionStatement) {
        // just visit the inside expression
        expressionStatement.expression.visit()
    }

    override fun visitEmptyStatement(o: RsEmptyStatement) {
        // NO-OP
    }

    override fun visitParExpression(parenthesizedExpression: RsParExpression) {
        val innerExpression = parenthesizedExpression.expression

        // relay the type hint to the inner expression and visit it
        innerExpression.typeHint = parenthesizedExpression.typeHint
        innerExpression.visit()

        // set the type to the type of what the expression evaluates to
        parenthesizedExpression.type = innerExpression.type
    }

    override fun visitConditionExpression(conditionExpression: RsConditionExpression) {
        val left = conditionExpression.left
        val right = conditionExpression.right
        val operator = conditionExpression.conditionOp

        // check for validation based on if we're within calc or condition.
        val validOperation = checkBinaryConditionOperation(left, operator, right)

        // early return if it isn't a valid operation
        if (!validOperation) {
            conditionExpression.type = MetaType.Error
            return
        }

        // conditions expect boolean
        conditionExpression.type = PrimitiveType.BOOLEAN
    }

    /**
     * Verifies the binary expression is a valid condition operation.
     */
    private fun checkBinaryConditionOperation(
        left: RsExpression,
        operator: RsConditionOp,
        right: RsExpression
    ): Boolean {
        // some operators expect a specific type on both sides, specify those type(s) here
        val allowedTypes = when (operator.text) {
            "&", "|" -> ALLOWED_LOGICAL_TYPES
            "<", ">", "<=", ">=" -> ALLOWED_RELATIONAL_TYPES
            else -> null
        }

        // if required type is set we should type hint with those, otherwise use the opposite
        // sides type as a hint.
        if (allowedTypes != null) {
            left.typeHint = allowedTypes.first()
            right.typeHint = allowedTypes.first()
        } else {
            // assign the type hints using the opposite side if it isn't already assigned.
            left.typeHint = if (left.typeHint != null) left.typeHint else right.nullableType
            right.typeHint = if (right.typeHint != null) right.typeHint else left.nullableType
        }

        // TODO better logic for this to allow things such as 'if (null ! $var)', should also revisit the above
        // visit left side to get the type for hinting to the right side if needed
        left.visit()

        // type hint right if not already hinted to the left type and then visit
        right.typeHint = right.typeHint ?: left.type
        right.visit()

        // verify the left and right type only return 1 type that is not 'unit'.
        if (left.type is TupleType || right.type is TupleType) {
            if (left.type is TupleType) {
                left.reportError(DiagnosticMessage.BINOP_TUPLE_TYPE, "Left", left.type.representation)
            }
            if (right.type is TupleType) {
                right.reportError(DiagnosticMessage.BINOP_TUPLE_TYPE, "Right", right.type.representation)
            }
            return false
        } else if (left.type == MetaType.Unit || right.type == MetaType.Unit) {
            operator.reportError(
                DiagnosticMessage.BINOP_INVALID_TYPES,
                operator.text,
                left.type.representation,
                right.type.representation,
            )
            return false
        }

        // handle operator specific required types, this applies to all except `!` and `=`.
        if (allowedTypes != null) {
            if (
                !checkTypeMatchAny(left, allowedTypes, left.type) ||
                !checkTypeMatchAny(right, allowedTypes, right.type)
            ) {
                operator.reportError(
                    DiagnosticMessage.BINOP_INVALID_TYPES,
                    operator.text,
                    left.type.representation,
                    right.type.representation,
                )
                return false
            }
        }

        // handle equality operator, which allows any type on either side as long as they match
        if (!checkTypeMatch(left, left.type, right.type, reportError = false)) {
            operator.reportError(
                DiagnosticMessage.BINOP_INVALID_TYPES,
                operator.text,
                left.type.representation,
                right.type.representation,
            )
            return false
        } else if (left.type == PrimitiveType.STRING && right.type == PrimitiveType.STRING) {
            operator.reportError(
                DiagnosticMessage.BINOP_INVALID_TYPES,
                operator.text,
                left.type.representation,
                right.type.representation,
            )
            return false
        }

        // other cases are true
        return true
    }

    override fun visitRelationalValueExpression(o: RsRelationalValueExpression) {
        val expr = o.expression
        if (expr != null) {
            expr.typeHint = o.typeHint
            expr.visit()
            o.type = expr.type
        }
    }

    override fun visitArithmeticExpression(arithmeticExpression: RsArithmeticExpression) {
        val left = arithmeticExpression.left
        val right = arithmeticExpression.right
        val operator = arithmeticExpression.arithmeticOp

        // arithmetic expression only expect int or long return types, but just allow
        val expectedType = when (val hint = arithmeticExpression.typeHint) {
            null -> PrimitiveType.INT
            else -> hint
        }

        // visit left-hand side
        left.typeHint = expectedType
        left.visit()

        // visit right-hand side
        right.typeHint = expectedType
        right.visit()

        // verify if both sides are int or long and are of the same type
        if (
            !checkTypeMatchAny(left, ALLOWED_ARITHMETIC_TYPES, left.type) ||
            !checkTypeMatchAny(left, ALLOWED_ARITHMETIC_TYPES, right.type) ||
            !checkTypeMatch(left, expectedType, left.type, reportError = false) ||
            !checkTypeMatch(right, expectedType, right.type, reportError = false)
        ) {
            operator.reportError(
                DiagnosticMessage.BINOP_INVALID_TYPES,
                operator.text,
                left.type.representation,
                right.type.representation,
            )
            arithmeticExpression.type = MetaType.Error
            return
        }

        arithmeticExpression.type = expectedType
    }

    override fun visitArithmeticValueExpression(o: RsArithmeticValueExpression) {
        val expr = o.expression
        if (expr != null) {
            expr.typeHint = o.typeHint
            expr.visit()
            o.type = expr.type
        }
    }

    override fun visitCalcExpression(calcExpression: RsCalcExpression) {
        val typeHint = calcExpression.typeHint ?: PrimitiveType.INT
        val innerExpression = calcExpression.expression

        // hint to the expression that we expect an int
        innerExpression.typeHint = typeHint
        innerExpression.visit()

        // verify type is an int
        if (!checkTypeMatchAny(innerExpression, ALLOWED_ARITHMETIC_TYPES, innerExpression.type)) {
            innerExpression.reportError(DiagnosticMessage.ARITHMETIC_INVALID_TYPE, innerExpression.type.representation)
            calcExpression.type = MetaType.Error
        } else {
            calcExpression.type = innerExpression.type
        }
    }

    override fun visitCommandExpression(commandCallExpression: RsCommandExpression) {
        val name = commandCallExpression.nameString

        // attempt to call the dynamic command handlers type checker (if one exists)
        if (checkDynamicCommand(name, commandCallExpression)) {
            return
        }

        // check the command call
        checkCallExpression(commandCallExpression, commandTrigger, DiagnosticMessage.COMMAND_REFERENCE_UNRESOLVED)
    }

    override fun visitGosubExpression(goSubExpression: RsGosubExpression) {
        // check the proc call
        checkCallExpression(goSubExpression, procTrigger, DiagnosticMessage.PROC_REFERENCE_UNRESOLVED)
    }


    /**
     * Runs the type checking for dynamic commands if one exists with [name].
     */
    private fun checkDynamicCommand(name: String, expression: RsExpression): Boolean {
        val dynamicCommand = dynamicCommands[name] ?: return false
        with(dynamicCommand) {
            // invoke the custom command type checking
            val context = TypeCheckingContext(this@TypeChecking, typeManager, expression, diagnostics)
            context.typeCheck()

            // verify the type has been set
            if (expression.nullableType == null) {
                expression.reportError(DiagnosticMessage.CUSTOM_HANDLER_NOTYPE)
            }
        }
        return true
    }

    /**
     * Handles looking up and type checking all call expressions.
     */
    private fun checkCallExpression(call: RsCallExpression, trigger: TriggerType, unresolvedSymbolMessage: String) {
        // lookup the symbol using the symbol type and name
        val name = call.nameLiteral?.text ?: ""
        val symbol = call.reference?.resolve()?.let { it as RsScript }
        if (symbol == null) {
            call.type = MetaType.Error
            call.reportError(unresolvedSymbolMessage, name)
        } else {
            call.type = symbol.computeReturnType(symbol.computeTriggerType())
        }

        // verify the arguments are all valid
        typeCheckArguments(symbol, call, name)
    }

    /**
     * Verifies that [callExpression] arguments match the parameter types from [symbol].
     */
    private fun typeCheckArguments(symbol: RsScript?, callExpression: RsCallExpression, name: String) {
        // Type check the parameters, use `unit` if there are no parameters
        // we will display a special message if the parameter ends up having unit
        // as the type but arguments are supplied.
        //
        // If the symbol is null then that means we failed to look up the symbol,
        // therefore we should specify the parameter types as error, so we can continue
        // analysis on all the arguments without worrying about a type mismatch.
        val parameterTypes = symbol.computeParameterType()
        val expectedTypes = if (parameterTypes is TupleType) {
            parameterTypes.children.toList()
        } else {
            listOf(parameterTypes)
        }
        val actualTypes = typeHintExpressionList(expectedTypes, callExpression.arguments)

        // convert the type lists into a singular type, used for type checking
        val expectedType = TupleType.fromList(expectedTypes)
        val actualType = TupleType.fromList(actualTypes)

        // special case for the temporary state of using unit for no arguments
        if (expectedType == MetaType.Unit && actualType != MetaType.Unit) {
            val errorMessage = when (callExpression) {
                is RsCommandExpression -> DiagnosticMessage.COMMAND_NOARGS_EXPECTED
                is RsGosubExpression -> DiagnosticMessage.PROC_NOARGS_EXPECTED
                is RsHookFragment -> DiagnosticMessage.CLIENTSCRIPT_NOARGS_EXPECTED
                else -> error(callExpression)
            }
            callExpression.reportError(
                errorMessage,
                name,
                actualType.representation,
            )
            return
        }

        // do the actual type checking
        checkTypeMatch(callExpression, expectedType, actualType)
    }

    /**
     * Type checks the index value expression if it is defined.
     */
    override fun visitLocalVariableExpression(localVariableExpression: RsLocalVariableExpression) {
        val name = localVariableExpression.nameLiteral.text
        val symbol = table.find(name)
        if (symbol == null) {
            // trying to reference a variable that isn't defined
            localVariableExpression.reportError(DiagnosticMessage.LOCAL_REFERENCE_UNRESOLVED, name)
            localVariableExpression.type = MetaType.Error
            return
        }

        // note(arrays-v2): array variables are able to be re-assigned, this also enables reference to arrays
        //                  with the dollar symbol prefix.
        if (!arraysV2 && symbol.type is ArrayType) {
            // trying to reference array variable without specifying the index in which to access
            localVariableExpression.reportError(DiagnosticMessage.LOCAL_ARRAY_REFERENCE_NOINDEX, name)
            localVariableExpression.type = MetaType.Error
            return
        }
        localVariableExpression.type = symbol.type
    }

    override fun visitScopedVariableExpression(gameVariableExpression: RsScopedVariableExpression) {
        val name = gameVariableExpression.nameLiteral.text
        val hint = gameVariableExpression.typeHint
        val symbol = RsSymbolIndex.lookupAll(gameVariableExpression, name).firstOrNull {
            symbolToType(it, hint) is GameVarType
        }
        if (symbol == null) {
            gameVariableExpression.type = MetaType.Error
            gameVariableExpression.reportError(DiagnosticMessage.GAME_REFERENCE_UNRESOLVED, name)
            return
        }
        val symType = symbolToType(symbol, hint) as GameVarType
        gameVariableExpression.type = symType.inner
    }

    /**
     * Type checks the index value expression if it is defined.
     */
    override fun visitArrayAccessExpression(arrayAccessExpression: RsArrayAccessExpression) {
        val name = (arrayAccessExpression.expressionList[0] as RsLocalVariableExpression).nameLiteral.text
        val symbol = table.find(name)
        if (symbol == null) {
            // trying to reference a variable that isn't defined
            arrayAccessExpression.reportError(DiagnosticMessage.LOCAL_REFERENCE_UNRESOLVED, name)
            arrayAccessExpression.type = MetaType.Error
            return
        }

        if (symbol.type !is ArrayType) {
            // trying to reference non-array local variable and specifying an index
            arrayAccessExpression.reportError(DiagnosticMessage.LOCAL_REFERENCE_NOT_ARRAY, name)
            arrayAccessExpression.type = MetaType.Error
            return
        }

        val indexExpression = arrayAccessExpression.expressionList.getOrNull(1)
        if (indexExpression != null) {
            // visit the index to set the type of any references
            indexExpression.visit()
            checkTypeMatch(indexExpression, PrimitiveType.INT, indexExpression.type)
        }

        // we are referring to a specific index within an array variable, so we need to
        // return the element type instead of the array type itself.
        arrayAccessExpression.type = symbol.type.inner
    }

    override fun visitIntegerLiteralExpression(integerLiteral: RsIntegerLiteralExpression) {
        val hint = integerLiteral.typeHint

        // this logic is a simplified version from string literals
        if (hint == null || hint == MetaType.Unit || typeManager.check(hint, PrimitiveType.INT)) {
            integerLiteral.type = PrimitiveType.INT
        } else if (hint !in LITERAL_TYPES) {
            resolveSymbol(integerLiteral, integerLiteral.text, hint)
        } else {
            integerLiteral.type = PrimitiveType.INT
        }
    }

    override fun visitCoordLiteralExpression(coordLiteral: RsCoordLiteralExpression) {
        coordLiteral.type = PrimitiveType.COORD
    }

    override fun visitBooleanLiteralExpression(booleanLiteral: RsBooleanLiteralExpression) {
        booleanLiteral.type = PrimitiveType.BOOLEAN
    }

//    override fun visitCharacterLiteralExpression(characterLiteral: RsCharacterLiteralExpression) {
//        characterLiteral.type = PrimitiveType.CHAR
//    }

    override fun visitNullLiteralExpression(nullLiteral: RsNullLiteralExpression) {
        val hint = nullLiteral.typeHint
        if (hint != null) {
            nullLiteral.type = hint
            return
        }
        nullLiteral.type = PrimitiveType.INT
    }

    override fun visitStringLiteralExpression(stringLiteral: RsStringLiteralExpression) {
        val hint = stringLiteral.typeHint
        // These ugly conditions are here to enable special cases.
        // 1) If the hint is a hook
        // 2) If the hint is not a string, and not any of the other types
        //    representable by a literal expression. It should be possible to
        //    reference a symbol via quoting it, this enables the ability to
        //    reference a symbol without it being a valid identifier.

        if (hint == null || typeManager.check(hint, PrimitiveType.STRING)) {
            // early check if string is assignable to hint
            // this mostly exists for when the expected type is `any`, we just
            // treat it as a string
            stringLiteral.type = PrimitiveType.STRING
        } else if (hint is MetaType.Hook) {
            stringLiteral.type = hint
            stringLiteral.hookScope = table.clone()
        } else if (hint !in LITERAL_TYPES) {
            resolveSymbol(stringLiteral, stringLiteral.text, hint)
        } else {
            stringLiteral.type = PrimitiveType.STRING
        }
    }

    override fun visitDynamicExpression(identifier: RsDynamicExpression) {
        val name = identifier.text
        val hint = identifier.typeHint

        // attempt to call the dynamic command handlers type checker (if one exists)
        if (checkDynamicCommand(name, identifier)) {
            return
        }

        // error is reported in resolveSymbol
        val symbol = resolveSymbol(identifier, name, hint) ?: return
        if (symbol is RsScript && symbol.computeTriggerType() == CommandTrigger) {
            val parameters = symbol.computeParameterType()
            if (parameters != MetaType.Unit) {
                identifier.reportError(
                    DiagnosticMessage.GENERIC_TYPE_MISMATCH,
                    "<unit>",
                    parameters.representation,
                )
            }
        }
    }

    override fun visitPrefixExpression(prefixExpression: RsPrefixExpression) {
        checkFixExpression(prefixExpression)
    }

    override fun visitPostfixExpression(postfixExpression: RsPostfixExpression) {
        checkFixExpression(postfixExpression)
    }

    private fun checkFixExpression(fixExpression: RsUnaryExpression) {
        val operator = fixExpression.unaryOp
        val variable = fixExpression.expression

        // these two checks should always be valid with current parse rules
        check(operator.text == "++" || operator.text == "--") { "Invalid operator: ${operator.text}" }
        check(variable is RsLocalVariableExpression || variable is RsArrayAccessExpression || variable is RsScopedVariableExpression) {
            "Invalid variable kind: $variable"
        }

        // disallow using fix operators on arrays
        if (variable is RsArrayAccessExpression) {
            variable.reportError(
                DiagnosticMessage.FIX_INVALID_VARIABLE_KIND,
                if (fixExpression is RsPrefixExpression) "Prefix" else "Postfix",
                operator.text,
            )
            fixExpression.type = MetaType.Error
            return
        }

        variable.visit()

        // since only increment/decrement is allowed, we need to check if the
        // expression type is an arithmetic allowed type
        if (!checkTypeMatchAny(variable, ALLOWED_ARITHMETIC_TYPES, variable.type)) {
            variable.reportError(
                DiagnosticMessage.FIX_OPERATOR_INVALID_TYPE,
                if (fixExpression is RsPrefixExpression) "Prefix" else "Postfix",
                operator.text,
                variable.type.representation,
            )
            fixExpression.type = MetaType.Error
            return
        }

        // set the type to the same as the operand
        fixExpression.type = variable.type
    }

    private fun resolveSymbol(node: RsExpression, name: String, hint: Type?): PsiElement? {
        val symbol = when (node) {
            is RsDynamicExpression -> RsDynamicExpressionReference.resolveElement(node, hint ?: MetaType.Any)
                .singleOrNull()?.element
            is RsStringLiteralExpression -> RsStringLiteralReference.resolveElement(node, hint ?: MetaType.Any)
                .singleOrNull()?.element
            is RsIntegerLiteralExpression -> RsIntegerLiteralReference.resolveElement(node, hint ?: MetaType.Any)
                .singleOrNull()?.element
            else -> null
        }
        if (symbol == null) {
            node.type = MetaType.Error
            node.reportError(DiagnosticMessage.GENERIC_UNRESOLVED_SYMBOL, name)
            return null
        }
        node.type = symbolToType(symbol, hint) ?: MetaType.Error
        return symbol

    }


    /**
     * Attempts to figure out the return type of [symbol].
     *
     * If the symbol is not valid for direct identifier lookup then `null` is returned.
     */
    private fun symbolToType(symbol: PsiElement, hint: Type?) = when (symbol) {
        is RsScript -> {
            val trigger = symbol.computeTriggerType()
            val returns = symbol.computeReturnType(trigger)
            if (trigger == CommandTrigger) {
                // only commands can be referenced by an identifier and return a value
                returns
            } else {
                // all other triggers get wrapped in a script type
                MetaType.Script(trigger!!, symbol.computeParameterType(), returns)
            }
        }

        is RsLocalVariableExpression -> symbol.type as? ArrayType
        is RsSymSymbol -> rawSymToType(symbol, typeManager, symbolLoaders)
        else -> error("Invalid symbol type: ${symbol::class.simpleName} for symbol: $symbol")
    }

    private fun RsSymField.parseAsType(): Type? {
        val text = text
        return typeManager.findOrNull(text)
    }

    override fun visitElement(node: PsiElement) {
        val parent = node.parent
        if (parent == null) {
            node.reportError("Unhandled node: %s.", node::class.simpleName!!)
        } else {
            node.reportError("Unhandled node: %s. Parent: %s", node::class.simpleName!!, parent::class.simpleName!!)
        }
    }

    /**
     * Takes [expectedTypes] and iterates over [expressions] assigning each [RsExpression.typeHint]
     * a type from [expectedTypes]. All of the [expressions] types are then returned for comparison
     * at call site.
     *
     * This is only useful when the expected types are known ahead of time (e.g. assignments and calls).
     */
    private fun typeHintExpressionList(expectedTypes: List<Type>, expressions: List<RsExpression>): List<Type> {
        val actualTypes = mutableListOf<Type>()
        var typeCounter = 0
        for (expr in expressions) {
            expr.typeHint = if (typeCounter < expectedTypes.size) expectedTypes[typeCounter] else null
            expr.visit()

            // add the evaluated type
            actualTypes += expr.type

            // increment the counter for type hinting
            typeCounter += if (expr.type is TupleType) {
                (expr.type as TupleType).children.size
            } else {
                1
            }
        }
        return actualTypes
    }

    /**
     * Checks if the [expected] and [actual] match, including accepted casting.
     *
     * If the types passed in are a [TupleType] they will be compared using their flattened types.
     *
     * @see TypeManager.check
     */
    internal fun checkTypeMatch(node: PsiElement, expected: Type, actual: Type, reportError: Boolean = true): Boolean {
        val expectedFlattened = if (expected is TupleType) expected.children else arrayOf(expected)
        val actualFlattened = if (actual is TupleType) actual.children else arrayOf(actual)

        var match = true
        // compare the flattened types
        if (expected == MetaType.Error) {
            // we need to do this to prevent error propagation due to expected type resolving to an error
            match = true
        } else if (expectedFlattened.size != actualFlattened.size) {
            match = false
        } else {
            for (i in expectedFlattened.indices) {
                match = match and typeManager.check(expectedFlattened[i], actualFlattened[i])
            }
        }

        if (!match && reportError) {
            val actualRepresentation = if (actual == MetaType.Unit) {
                "<unit>"
            } else {
                actual.representation
            }
            node.reportError(
                DiagnosticMessage.GENERIC_TYPE_MISMATCH,
                actualRepresentation,
                expected.representation,
            )
        }
        return match
    }

    /**
     * Checks if the [actual] matches any of [expected], including accepted casting.
     *
     * If the types passed in are a [TupleType] they will be compared using their flattened types.
     *
     * @see TypeManager.check
     */
    private fun checkTypeMatchAny(node: PsiElement, expected: Array<out Type>, actual: Type): Boolean {
        for (type in expected) {
            if (checkTypeMatch(node, type, actual, false)) {
                return true
            }
        }
        return false
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.INFO].
     */
    private fun PsiElement.reportInfo(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.INFO, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.WARNING].
     */
    private fun PsiElement.reportWarning(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.WARNING, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.ERROR].
     */
    private fun PsiElement.reportError(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.ERROR, this, message, *args))
    }

    /**
     * Shortcut to [PsiElement.accept] for nullable nodes.
     */
    fun PsiElement?.visit() {
        if (this == null) {
            return
        }
        accept(this@TypeChecking)
    }

    /**
     * Calls [PsiElement.accept] on all nodes in a list.
     */
    fun List<PsiElement>?.visit() {
        if (this == null) {
            return
        }

        for (n in this) {
            n.visit()
        }
    }

    /**
     * Finds all [Diagnostic]s that are of type [DiagnosticType.ERROR] and are associated with the given [element].
     */
    fun findErrors(element: PsiElement) = diagnostics.diagnostics.filter {
        it.isError() && it.element === element
    }


    /**
     * Computes the [TriggerType] for the given [RsScript] based on its trigger name expression.
     */
    fun RsScript?.computeTriggerType(): TriggerType? {
        val text = this?.triggerNameExpression?.text ?: return null
        return triggerManager.findOrNull(text)
    }

    /**
     * Computes the parameter types for the given [RsScript] based on its parameter list.
     */
    fun RsScript?.computeParameterType(): Type {
        val parameters = this?.parameterList?.parameterList?.map {
            val text = it.typeName.text
            val type = typeManager.findOrNull(text, allowArray = true)
            type ?: MetaType.Error
        }
        return TupleType.fromList(parameters)
    }

    /**
     * Computes the return type for the given [RsScript] based on its return list and the provided [TriggerType].
     */
    fun RsScript?.computeReturnType(trigger: TriggerType?): Type {
        val returnTokens = this?.returnList?.typeNameList
        return if (returnTokens.isNullOrEmpty()) {
            // default return based on trigger if the trigger was found
            // triggers that allow returns will default to `unit` instead of `nothing`.
            if (trigger == null) {
                MetaType.Error
            } else if (trigger.allowReturns) {
                MetaType.Unit
            } else {
                MetaType.Nothing
            }
        } else {
            val returns = mutableListOf<Type>()
            for (token in returnTokens) {
                val type = typeManager.findOrNull(token.text, allowArray = arraysV2)
                returns += type ?: MetaType.Error
            }
            TupleType.fromList(returns)
        }
    }

    private companion object {
        /**
         * Array of valid types allowed in logical conditional expressions.
         */
        private val ALLOWED_LOGICAL_TYPES = arrayOf(
            PrimitiveType.BOOLEAN,
        )

        /**
         * Array of valid types allowed in relational conditional expressions.
         */
        private val ALLOWED_RELATIONAL_TYPES = arrayOf(
            PrimitiveType.INT,
            PrimitiveType.LONG,
        )

        /**
         * Array of valid types allowed in arithmetic expressions.
         */
        private val ALLOWED_ARITHMETIC_TYPES = arrayOf(
            PrimitiveType.INT,
            PrimitiveType.LONG,
        )

        /**
         * Set of types that have a literal representation.
         */
        private val LITERAL_TYPES = setOf(
            PrimitiveType.INT,
            PrimitiveType.BOOLEAN,
            PrimitiveType.COORD,
            PrimitiveType.STRING,
            PrimitiveType.CHAR,
            PrimitiveType.LONG,
        )
    }
}