package io.runescript.plugin.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.*

class RsHighlightingAnnotator : Annotator{
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        element.accept(object: RuneScriptVisitor() {

            override fun visitScriptName(o: RuneScriptScriptName) {
                super.visitScriptName(o)
                element.highlight(holder, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
            }

            override fun visitLocalVariableExpression(o: RuneScriptLocalVariableExpression) {
                super.visitLocalVariableExpression(o)
                element.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            override fun visitArrayVariableExpression(o: RuneScriptArrayVariableExpression) {
                super.visitArrayVariableExpression(o)
                element.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            override fun visitCommandExpression(o: RuneScriptCommandExpression) {
                super.visitCommandExpression(o)
                o.nameLiteral.highlight(holder, RsSyntaxHighlighterColors.COMMAND_CALL)
            }

            override fun visitGosubExpression(o: RuneScriptGosubExpression) {
                super.visitGosubExpression(o)
                o.nameLiteral.highlight(holder, RsSyntaxHighlighterColors.PROC_CALL)
            }
        })
    }

    private fun PsiElement.highlight(holder: AnnotationHolder, attribute: TextAttributesKey) {
        holder.newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
            .range(textRange)
            .textAttributes(attribute)
            .create()
    }
}