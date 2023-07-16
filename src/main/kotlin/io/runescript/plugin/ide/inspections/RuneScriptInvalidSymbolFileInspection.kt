package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.incorrectFormatting.ReformatQuickFix
import com.intellij.codeInspection.incorrectFormatting.ShowDetailedReportIntention
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.guessModuleDir
import com.intellij.psi.PsiFile
import io.runescript.plugin.ide.config.RsConfig
import io.runescript.plugin.symbollang.psi.RsSymFile

class RuneScriptInvalidSymbolFileInspection : LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        if (file !is RsSymFile) {
            return null
        }
        val module = ModuleUtilCore.findModuleForPsiElement(file)
        val moduleDir = module?.guessModuleDir() ?: return null
        val symbolsDir = moduleDir.findChild("symbols") ?:  return null
        val fileDir = file.containingDirectory ?:  return null
        if (fileDir.virtualFile != symbolsDir) {
            return null
        }
        val name = file.containingFile.name
        val typeName = name.substring(0, name.indexOf('.'))
        if (typeName !in RsConfig.getPrimitiveTypes(null)) {
            return arrayOf(
                manager.createProblemDescriptor(
                    file,
                    "Unrecognized symbol file type: $typeName",
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