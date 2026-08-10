package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsEmptyStatement
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsStatement
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.RsWhileStatement

class RuneScriptEmptyControlFlowBodyInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor =
        object : RsVisitor() {
            override fun visitIfStatement(o: RsIfStatement) {
                o.trueStatement?.takeIf { it.isEmptyBody() }?.let {
                    holder.registerProblem(it, "Empty 'if' body", ProblemHighlightType.WEAK_WARNING)
                }
                o.falseStatement?.takeIf { it !is RsIfStatement && it.isEmptyBody() }?.let {
                    holder.registerProblem(it, "Empty 'else' body", ProblemHighlightType.WEAK_WARNING)
                }
            }

            override fun visitWhileStatement(o: RsWhileStatement) {
                o.statement?.takeIf { it.isEmptyBody() }?.let {
                    holder.registerProblem(it, "Empty 'while' body", ProblemHighlightType.WEAK_WARNING)
                }
            }

            override fun visitSwitchCase(o: RsSwitchCase) {
                if (o.statementList.statementList.all { it is RsEmptyStatement } && !o.hasCommentBeforeNextLabel()) {
                    holder.registerProblem(o.`case`, "Empty switch case body", ProblemHighlightType.WEAK_WARNING)
                }
            }
        }

    private fun RsStatement.isEmptyBody(): Boolean =
        this is RsEmptyStatement ||
            (this is RsBlockStatement && statementList.statementList.all { it is RsEmptyStatement } && !hasComment())

    private fun com.intellij.psi.PsiElement.hasComment(): Boolean = PsiTreeUtil.findChildOfType(this, PsiComment::class.java) != null

    private fun RsSwitchCase.hasCommentBeforeNextLabel(): Boolean {
        val switch = parent as? RsSwitchStatement ?: return false
        val cases = switch.switchCaseList
        val nextOffset = cases.getOrNull(cases.indexOf(this) + 1)?.textOffset ?: switch.rbrace?.textOffset ?: return false
        val bodyStartOffset = colon.textRange.endOffset
        return PsiTreeUtil.findChildrenOfType(switch, PsiComment::class.java).any {
            it.textOffset in bodyStartOffset..<nextOffset
        }
    }
}
