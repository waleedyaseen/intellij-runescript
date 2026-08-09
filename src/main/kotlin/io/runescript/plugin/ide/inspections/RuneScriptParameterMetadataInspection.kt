package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.ide.parameter.RsParameterBehavior
import io.runescript.plugin.ide.parameter.RsParameterBehaviorResolver
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.doc.psi.impl.RsDocTag
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RuneScriptParameterMetadataInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitScript(o: RsScript) {
                val parameters =
                    o.parameterList
                        ?.parameterList
                        .orEmpty()
                        .mapNotNull { parameter -> parameter.localVariableExpression?.name?.let { name -> name to parameter } }
                        .toMap()
                val tags =
                    o
                        .findDoc()
                        ?.getAllSections()
                        .orEmpty()
                        .flatMap { section -> section.findTagsByName(RsParameterBehaviorResolver.PARAMETER_METADATA_TAG) }
                val seen = mutableSetOf<Pair<String, String>>()

                for (tag in tags) {
                    inspectTag(o, tag, parameters, seen, holder)
                }
            }
        }

    private fun inspectTag(
        script: RsScript,
        tag: RsDocTag,
        parameters: Map<String, RsParameter>,
        seen: MutableSet<Pair<String, String>>,
        holder: ProblemsHolder,
    ) {
        val subject = tag.getSubjectName()
        if (subject == null) {
            holder.registerProblem(tag, "Parameter metadata must name a parameter")
            return
        }
        val subjectElement = tag.subjectElement()
        val parameter = parameters[subject]
        if (parameter == null) {
            val nearest = parameters.keys.minByOrNull { candidate -> editDistance(subject, candidate) }
            val fixes = nearest?.let { name -> arrayOf<LocalQuickFix>(RsRenameMetadataParameterQuickFix(name)) }.orEmpty()
            holder.registerProblem(subjectElement ?: tag, "Unknown parameter '$subject'", *fixes)
            return
        }

        val behavior = RsParameterBehaviorResolver.parseBehavior(tag.getContent())
        if (behavior == null) {
            holder.registerProblem(tag, "Malformed parameter behavior")
            return
        }
        if (behavior.id !in SUPPORTED_BEHAVIORS) {
            holder.registerProblem(tag, "Unknown parameter behavior '${behavior.id}'")
            return
        }
        if (!seen.add(subject to behavior.id)) {
            holder.registerProblem(tag, "Duplicate '${behavior.id}' behavior for '$subject'")
        }

        when (behavior.id) {
            RGB_BEHAVIOR,
            ARGB_BEHAVIOR,
            -> inspectColorBehavior(tag, parameter, behavior, holder)

            CONSTANT_BEHAVIOR -> inspectConstantBehavior(script, tag, behavior, holder)
        }
    }

    private fun inspectColorBehavior(
        tag: RsDocTag,
        parameter: RsParameter,
        behavior: RsParameterBehavior,
        holder: ProblemsHolder,
    ) {
        if (parameter.typeName.text != "int") {
            holder.registerProblem(tag, "'${behavior.id}' behavior requires an int parameter")
        }
        if (behavior.options.isNotEmpty()) {
            holder.registerProblem(tag, "'${behavior.id}' behavior does not accept options")
        }
    }

    private fun inspectConstantBehavior(
        script: RsScript,
        tag: RsDocTag,
        behavior: RsParameterBehavior,
        holder: ProblemsHolder,
    ) {
        if (behavior.options.isEmpty()) {
            holder.registerProblem(tag, "'constant' behavior requires at least one constant")
            return
        }
        for (option in behavior.options) {
            val name = option.removePrefix("^")
            if (RsSymbolIndex.lookup(script, "constant", name) == null) {
                holder.registerProblem(tag, "Unknown constant '^$name'")
            }
        }
    }

    private fun RsDocTag.subjectElement(): PsiElement? =
        getSubjectLink()?.let { link -> PsiTreeUtil.findChildOfType(link, RsDocName::class.java) }

    private companion object {
        const val RGB_BEHAVIOR = "rgb"
        const val ARGB_BEHAVIOR = "argb"
        const val CONSTANT_BEHAVIOR = "constant"
        val SUPPORTED_BEHAVIORS = setOf(RGB_BEHAVIOR, ARGB_BEHAVIOR, CONSTANT_BEHAVIOR)
    }
}

private class RsRenameMetadataParameterQuickFix(
    private val newName: String,
) : LocalQuickFix {
    override fun getFamilyName(): String = "Rename metadata parameter"

    override fun getName(): String = "Change metadata parameter to '$newName'"

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val name = descriptor.psiElement as? RsDocName ?: return
        ElementManipulators.handleContentChange(name, newName)
    }
}

private fun editDistance(
    left: String,
    right: String,
): Int {
    var previous = IntArray(right.length + 1) { index -> index }
    for (leftIndex in left.indices) {
        val current = IntArray(right.length + 1)
        current[0] = leftIndex + 1
        for (rightIndex in right.indices) {
            val substitutionCost = if (left[leftIndex] == right[rightIndex]) 0 else 1
            current[rightIndex + 1] =
                minOf(
                    current[rightIndex] + 1,
                    previous[rightIndex + 1] + 1,
                    previous[rightIndex] + substitutionCost,
                )
        }
        previous = current
    }
    return previous[right.length]
}
