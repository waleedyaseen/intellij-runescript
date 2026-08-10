package io.runescript.plugin.ide.inspections.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsAddMissingDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsRemoveObsoleteDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsReorderDocTagsIntention
import io.runescript.plugin.lang.psi.RsScript

class RsMaintainDocTagsQuickFix(
    private val kind: Kind,
) : LocalQuickFix {
    override fun getFamilyName(): String = "Maintain RSDoc signature tags"

    override fun getName(): String = kind.fixName

    override fun applyFix(
        project: Project,
        descriptor: ProblemDescriptor,
    ) {
        val element = descriptor.psiElement
        val script = element as? RsScript ?: PsiTreeUtil.getParentOfType(element, RsScript::class.java, false) ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(script.containingFile) ?: return
        when (kind) {
            Kind.ADD_MISSING -> RsAddMissingDocTagsIntention().applyTo(project, document, script)
            Kind.REMOVE_OBSOLETE -> RsRemoveObsoleteDocTagsIntention().applyTo(project, document, script)
            Kind.REORDER -> RsReorderDocTagsIntention().applyTo(project, document, script)
        }
    }

    enum class Kind(
        val fixName: String,
    ) {
        ADD_MISSING("Add missing RSDoc tags"),
        REMOVE_OBSOLETE("Remove obsolete RSDoc tags"),
        REORDER("Reorder RSDoc tags"),
    }
}
