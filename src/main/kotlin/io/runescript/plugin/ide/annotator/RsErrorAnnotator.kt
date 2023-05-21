package io.runescript.plugin.ide.annotator

import com.intellij.codeInsight.daemon.impl.HighlightRangeExtension
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import io.runescript.plugin.lang.psi.*

class RsErrorAnnotator : Annotator, HighlightRangeExtension {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    }

    override fun isForceHighlightParents(file: PsiFile) = file is RsFile

    class RsErrorVisitor(private val annotator: RsErrorAnnotator) : RsVisitor() {

        override fun visitFile(file: PsiFile) {
            super.visitFile(file)
        }

        override fun visitScript(o: RsScript) {
            super.visitScript(o)
        }

        override fun visitScriptHeader(o: RsScriptHeader) {
            super.visitScriptHeader(o)
        }

        override fun visitScriptName(o: RsScriptName) {

            super.visitScriptName(o)
        }
    }
}