package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElementVisitor
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RuneScriptMissingScriptSymbolInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : RsVisitor() {

            override fun visitScript(o: RsScript) {
                if (!o.isSourceFile() || o.triggerName == "command") return
                val name = o.qualifiedName
                val length = o.rbracket.endOffset - o.lbracket.startOffset
                val range = TextRange.from(o.lbracket.startOffsetInParent, length)
                if (RsSymbolIndex.lookup(o.project, RsPrimitiveType.CLIENTSCRIPT, name) == null) {
                    holder.registerProblem(
                        o,
                        RsBundle.message("inspection.error.script.symbol.not.found", name),
                        ProblemHighlightType.ERROR,
                        range
                    )
                }
            }
        }
    }
}