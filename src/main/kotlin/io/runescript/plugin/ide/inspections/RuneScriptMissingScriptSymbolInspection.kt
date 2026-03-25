package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.isSourceFile
import io.runescript.plugin.lang.psi.qualifiedName
import io.runescript.plugin.lang.psi.triggerName

class RuneScriptMissingScriptSymbolInspection : LocalInspectionTool() {
    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
    ): PsiElementVisitor {
        return object : RsVisitor() {
            override fun visitScript(o: RsScript) {
                if (!o.isSourceFile() || o.triggerName == "command") return
                val name = o.qualifiedName
                val length = o.rbracket.endOffset - o.lbracket.startOffset
                val range = TextRange.from(o.lbracket.startOffsetInParent, length)
//                if (RsSymbolIndex.lookup(o, RsPrimitiveType.CLIENTSCRIPT, name) == null) {
//                    holder.registerProblem(
//                        o,
//                        RsBundle.message("inspection.error.script.symbol.not.found", name),
//                        ProblemHighlightType.ERROR,
//                        range
//                    )
//                } TODO(NOPUSH):
            }
        }
    }
}
