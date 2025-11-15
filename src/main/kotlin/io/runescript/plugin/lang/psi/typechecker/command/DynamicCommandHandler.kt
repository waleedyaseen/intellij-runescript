package io.runescript.plugin.lang.psi.typechecker.command

import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.typechecker.TypeCheckingContext
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticType
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics


/**
 * A dynamic command handler allows more complex commands to be implemented.
 * Implementations are able to do custom type checking and code generations,
 * which allows for some commands to be implemented properly.
 *
 * A lot of implementations may not need to supply custom code generation,
 * which in that case they can omit [generateCode].
 */
interface DynamicCommandHandler {
    /**
     * Handles type checking the expression. The expression will only ever be [CallExpression] or [Identifier].
     *
     * All implementations should follow these basic rules:
     *  - `expression.type` **must** be defined.
     *  - If `expression.symbol` is not defined, an attempt is made to look up a predefined symbol in the root
     *  table. If a predefined symbol wasn't found an internal compiler error will be thrown.
     *  - Errors should be reported using [PsiElement.reportError].
     */
    fun TypeCheckingContext.typeCheck()

    // helper functions

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.INFO].
     */
    fun PsiElement.reportInfo(diagnostics: Diagnostics, message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.INFO, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.WARNING].
     */
    fun PsiElement.reportWarning(diagnostics: Diagnostics, message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.WARNING, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.ERROR].
     */
    fun PsiElement.reportError(diagnostics: Diagnostics, message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.ERROR, this, message, *args))
    }
}
