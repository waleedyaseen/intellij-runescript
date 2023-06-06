package io.runescript.plugin.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import io.runescript.plugin.lang.psi.*

class RsHighlightingAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(object : RsVisitor() {

            override fun visitScriptName(o: RsScriptName) {
                super.visitScriptName(o)
                element.highlight(holder, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
            }

            override fun visitLocalVariableExpression(o: RsLocalVariableExpression) {
                super.visitLocalVariableExpression(o)
                element.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            override fun visitArrayAccessExpression(o: RsArrayAccessExpression) {
                o.expressionList[0].highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            override fun visitScopedVariableExpression(o: RsScopedVariableExpression) {
                super.visitScopedVariableExpression(o)
                element.highlight(holder, RsSyntaxHighlighterColors.SCOPED_VARIABLE)
            }

            override fun visitCommandExpression(o: RsCommandExpression) {
                super.visitCommandExpression(o)
                o.nameLiteral.highlight(holder, RsSyntaxHighlighterColors.COMMAND_CALL)
            }

            override fun visitGosubExpression(o: RsGosubExpression) {
                super.visitGosubExpression(o)
                val textRange = TextRange(o.tilde.startOffset, o.nameLiteral.endOffset)
                textRange.highlight(holder, RsSyntaxHighlighterColors.PROC_CALL)
            }
        })
    }

    private fun PsiElement.highlight(holder: AnnotationHolder, attribute: TextAttributesKey) {
        textRange.highlight(holder, attribute)
    }

    private fun TextRange.highlight(holder: AnnotationHolder, attribute: TextAttributesKey) {
        holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
            .range(this)
            .textAttributes(attribute)
            .create()
    }
}