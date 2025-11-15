package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsNameLiteral
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticType
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.type.TypeManager

/**
 * Contains the context of the [TypeChecking] and supplies useful functions when
 * implementing a [DynamicCommandHandler].
 */
data class TypeCheckingContext(
    private val typeChecker: TypeChecking,
    val typeManager: TypeManager,
    val expression: RsExpression,
    val diagnostics: Diagnostics,
) {
    /**
     * Returns a list of expressions that were passed to the expression as arguments.
     *
     * This returns [CallExpression.arguments] if the expression is a [CallExpression],
     * otherwise an empty list.
     */
    val RsExpression?.arguments: List<RsExpression>
        get() {
            if (this is RsCallExpression) {
                return argumentList?.expressionList ?: emptyList()
            }
            return emptyList()
        }

    /**
     * Whether the expression is a constant expression.
     *
     * A constant expression is defined as being one of the following:
     *  - [ConstantVariableExpression]
     *  - [Literal]
     *  - [Identifier] (see note below)
     *
     * Note: Identifiers that reference symbols other than [io.runescript.plugin.lang.psi.typechecker.symbol.BasicSymbol] do not
     * quality as a constant expression.
     */
//    val RsExpression?.isConstant: Boolean
//        get() {
//            if (this == null) {
//                return false
//            }
//            return typeChecker.isConstantExpression(this)
//        }

    /**
     * Checks the argument at [index]. If the argument exists then the `typeHint` of the
     * expression is set to [typeHint] and the argument is then passed through the visitor like
     * normal. Accessing `type` after this is safe as long as returned value is not `null`. The
     * returned value will only be `null` if the argument requested is out of bounds.
     *
     * Using [args2] will check [CommandCallExpression.arguments2] instead of [arguments].
     *
     * Example:
     * ```
     * // check the argument with a type hint of obj
     * checkArgument(0, typeHint = PrimitiveType.OBJ)
     *
     * // verify the types match, if mismatch let the function report the error
     * checkArgumentTypes(expected = PrimitiveType.OBJ)
     * ```
     *
     * @see checkTypeArgument
     */
    fun checkArgument(index: Int, typeHint: Type?, args2: Boolean = false): RsExpression? {
        val arguments = getArgumentsList(args2)
        if (index !in arguments.indices) {
            return null
        }
        val argument = arguments[index]
        argument.visit(typeHint)
        return argument
    }

    /**
     * Helper function that returns the requested argument list.
     */
    private fun getArgumentsList(args2: Boolean = false): List<RsExpression> =
        if (args2 && expression is RsCommandExpression) {
            expression.args2?.expressionList ?: emptyList()
        } else {
            expression.arguments
        }

    /**
     * Checks the argument at [index]. If the argument exists is it validated to be a basic
     * [Identifier] and attempts to look up a type based on the identifier text. If the
     * argument does not exist (out of bounds), `null` is returned.
     *
     * If the expression is not an identifier or the type does not exist an error is logged.
     * The expressions type is assigned to either [MetaType.Error] in the case of error,
     * and [MetaType.Type] if successful.
     *
     * This should only be used when attempting to evaluate an argument as a type reference.
     *
     * @see checkArgument
     */
    fun checkTypeArgument(index: Int): RsExpression? {
        if (index !in expression.arguments.indices) {
            return null
        }

        val argument = expression.arguments[index]

        // verify the argument is a constant expression
        if (argument !is RsDynamicExpression) {
            diagnostics.report(Diagnostic(DiagnosticType.ERROR, argument, DIAGNOSTIC_TYPEREF_EXPECTED))
            argument.type = MetaType.Error
            return argument
        }

        // lookup the type by name
        val type = typeManager.findOrNull(argument.text)
        if (type == null) {
            // type doesn't exist so report an error
            diagnostics.report(
                Diagnostic(
                    DiagnosticType.ERROR,
                    argument,
                    DiagnosticMessage.GENERIC_INVALID_TYPE,
                    argument.text,
                ),
            )
            argument.type = MetaType.Error
            return argument
        }

        // assign the type and create a basic symbol with the type name and wrapped type
        argument.type = MetaType.Type(type)
//        argument.reference = BasicSymbol(argument.text, argument.type)
        return argument
    }

    /**
     * Verifies that the command argument types matches [expected]. This function
     * should be used  for validation the argument types passed to the command.
     * When needing to compare multiple types, use [TupleType].
     *
     * If [reportError] is enabled and there is a type mismatch, an error is submitted to the
     * [diagnostics]. If this option is disabled, you _must_ use the return value to report
     * an error manually otherwise compilation will continue even though there was an error.
     *
     * The following example is type hinting to `int` and visiting the first argument if it exists.
     * Then it if verifying that the arguments passed to the command actually matches a single `int`.
     * ```
     * // check the argument while type hinting it as an int
     * checkArgument(0, typeHint = PrimitiveType.INT)
     *
     * // actually verify the arguments match to a single int
     * checkArgumentTypes(expected = PrimitiveType.INT)
     * ```
     */
    fun checkArgumentTypes(expected: Type, reportError: Boolean = true, args2: Boolean = false): Boolean {
        val arguments = getArgumentsList(args2)

        // collect the argument types while visiting any arguments that have no type defined
        val argumentTypes = mutableListOf<Type>()
        for (arg in arguments) {
            if (arg.nullableType == null) {
                arg.visit()
            }
            argumentTypes += arg.type
        }

        val actual = TupleType.fromList(argumentTypes)
        return typeChecker.checkTypeMatch(expression, expected, actual, reportError)
    }


    /**
     * Collects all [Expression.type] for the [expressions].
     *
     * @see TupleType.fromList
     */
    fun collectTypes(vararg expressions: RsExpression?): Type =
        TupleType.fromList(expressions.mapNotNull { it?.nullableType })

    /**
     * Passes the node through the type checker if it is not `null`.
     */
    fun PsiElement?.visit() {
        typeChecker.run {
            visit()
        }
    }

    /**
     * Passes the expression through the type check if it is not `null`.
     */
    fun RsExpression?.visit(typeHint: Type?) {
        typeChecker.run {
            if (typeHint != null) {
                this@visit?.typeHint = typeHint
            }
            visit()
        }
    }

    /**
     * Passes all nodes within the list through the type check if it is not `null`.
     */
    fun List<PsiElement>?.visit() {
        typeChecker.run {
            visit()
        }
    }

    private companion object {
        const val DIAGNOSTIC_TYPEREF_EXPECTED = "Type reference expected."
    }
}
