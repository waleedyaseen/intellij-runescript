package io.runescript.plugin.lang.psi.typechecker.diagnostics

import com.intellij.psi.PsiElement

// TODO docs
data class Diagnostic(
    val type: DiagnosticType,
    val element: PsiElement,
    val message: String,
    val messageArgs: List<Any>,
) {
    constructor(
        type: DiagnosticType,
        node: PsiElement,
        message: String,
        vararg messageArgs: Any,
    ) : this(type, node, message, messageArgs.toList())

    fun isError(): Boolean = type == DiagnosticType.ERROR || type == DiagnosticType.SYNTAX_ERROR
}
