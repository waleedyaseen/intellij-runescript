package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.parameter.RsParameterBehaviorRegistry
import io.runescript.plugin.ide.parameter.RsParameterBehaviorResolver
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RuneScriptParameterConstantInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitExpression(o: RsExpression) {
                val behavior = RsParameterBehaviorResolver.find(o, RsParameterBehaviorRegistry.CONSTANT_ID) ?: return
                val allowedNames = behavior.options.map { option -> option.removePrefix("^") }.distinct()
                if (allowedNames.isEmpty()) return

                val constantName = (o as? RsConstantExpression)?.name
                if (constantName in allowedNames) return

                val argumentValue =
                    when (o) {
                        is RsIntegerLiteralExpression -> o.text.parseInteger()
                        is RsConstantExpression -> o.name?.let { name -> resolveConstantValue(o, name) }
                        else -> null
                    }
                val matchingNames =
                    if (argumentValue == null) {
                        emptyList()
                    } else {
                        allowedNames.filter { name -> resolveConstantValue(o, name) == argumentValue }
                    }
                val suggestedNames = matchingNames.ifEmpty { allowedNames }
                val fixes = suggestedNames.map(::RsReplaceWithConstantQuickFix).toTypedArray()
                holder.registerProblem(
                    o,
                    "Value must be one of: ${allowedNames.joinToString { name -> "^$name" }}",
                    *fixes,
                )
            }
        }

    private fun resolveConstantValue(
        context: RsExpression,
        name: String,
    ): Long? {
        val symbol = RsSymbolIndex.lookup(context, "constant", name) ?: return null
        return symbol.constantValue()?.parseInteger()
    }

    private fun RsSymSymbol.constantValue(): String? = fieldList.lastOrNull()?.text
}

private class RsReplaceWithConstantQuickFix(
    private val constantName: String,
) : LocalQuickFix {
    override fun getFamilyName(): String = "Replace with allowed constant"

    override fun getName(): String = "Replace with ^$constantName"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val expression = descriptor.psiElement as? RsExpression ?: return
        expression.replace(RsElementGenerator.createExpression(project, "^$constantName"))
    }
}

private fun String.parseInteger(): Long? {
    val text = trim()
    return when {
        text.startsWith("-0x", ignoreCase = true) -> text.substring(3).toLongOrNull(16)?.let { value -> -value }
        text.startsWith("0x", ignoreCase = true) -> text.substring(2).toLongOrNull(16)
        else -> text.toLongOrNull()
    }
}
