package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsAddMissingDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsRemoveObsoleteDocTagsIntention
import io.runescript.plugin.ide.codeInsight.intention.documentation.RsReorderDocTagsIntention
import io.runescript.plugin.ide.inspections.fixes.RsMaintainDocTagsQuickFix
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor

class RuneScriptDocConsistencyInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitScript(o: RsScript) {
                val anchor = o.nameLiteralList.lastOrNull() ?: o
                if (RsAddMissingDocTagsIntention().isNeeded(o)) {
                    holder.registerProblem(
                        anchor,
                        "RSDoc is missing signature tags",
                        RsMaintainDocTagsQuickFix(RsMaintainDocTagsQuickFix.Kind.ADD_MISSING),
                    )
                }
                if (RsRemoveObsoleteDocTagsIntention().isNeeded(o)) {
                    holder.registerProblem(
                        anchor,
                        "RSDoc contains obsolete signature tags",
                        RsMaintainDocTagsQuickFix(RsMaintainDocTagsQuickFix.Kind.REMOVE_OBSOLETE),
                    )
                }
                if (RsReorderDocTagsIntention().isNeeded(o)) {
                    holder.registerProblem(
                        anchor,
                        "RSDoc tags do not match signature order",
                        RsMaintainDocTagsQuickFix(RsMaintainDocTagsQuickFix.Kind.REORDER),
                    )
                }
            }
        }
}
