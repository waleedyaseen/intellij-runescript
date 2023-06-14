package io.runescript.plugin.symbollang.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.guessModuleDir
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.config.RsConfig
import io.runescript.plugin.symbollang.psi.RsSymFile

class RsSymAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is RsSymFile) {
            return
        }
        val module = ModuleUtilCore.findModuleForPsiElement(element)
        val moduleDir = module?.guessModuleDir() ?: return
        val symbolsDir = moduleDir.findChild("symbols") ?: return
        val fileDir = element.containingDirectory ?: return
        if (fileDir.virtualFile != symbolsDir) {
            return
        }
        val name = element.containingFile.name
        val typeName = name.substring(0, name.indexOf('.'))
        if (typeName !in RsConfig.getPrimitiveTypes(null)) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unrecognized symbol file type: $typeName")
                .create()
        }
    }
}