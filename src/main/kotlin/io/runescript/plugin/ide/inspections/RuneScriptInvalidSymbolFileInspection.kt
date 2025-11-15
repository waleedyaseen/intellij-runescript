package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.psi.PsiFile
import io.runescript.plugin.symbollang.psi.RsSymFile
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName

class RuneScriptInvalidSymbolFileInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is RsSymFile) {
            return null
        }
        if (resolveToSymTypeName(file) == null) {
            return arrayOf(
                manager.createProblemDescriptor(
                    file,
                    "Unrecognized symbol file",
                    arrayOf(),
                    ProblemHighlightType.ERROR,
                    isOnTheFly,
                    false
                )
            )
        }
        return null
    }
}
