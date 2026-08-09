package io.runescript.plugin.ide.highlight

import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.endOffset
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.triggerName
import io.runescript.plugin.lang.psi.typechecker.typeCheckedResolvedSymbol
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsHighlightingAnnotator : Annotator {
    override fun annotate(
        element: PsiElement,
        holder: AnnotationHolder,
    ) {
        when (element) {
            is RsScript -> {
                TextRange
                    .create(element.lbracket.startOffset, element.rbracket.endOffset)
                    .highlight(holder, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
            }

            is RsConstantExpression -> {
                element.highlight(holder, RsSyntaxHighlighterColors.CONSTANT)
            }

            is RsLocalVariableExpression -> {
                element.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            is RsArrayAccessExpression -> {
                element.expressionList.firstOrNull()?.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            is RsScopedVariableExpression -> {
                element.highlight(holder, RsSyntaxHighlighterColors.SCOPED_VARIABLE)
            }

            is RsCommandExpression -> {
                element.nameLiteral.highlight(holder, RsSyntaxHighlighterColors.COMMAND_CALL)
            }

            is RsGosubExpression -> {
                TextRange(element.tilde.startOffset, element.nameLiteral.endOffset)
                    .highlight(holder, RsSyntaxHighlighterColors.PROC_CALL)
            }

            is RsHookFragment -> {
                element.nameLiteral.highlight(holder, RsSyntaxHighlighterColors.CLIENTSCRIPT_CALL)
            }

            is RsDynamicExpression -> {
                highlightDynamic(element, holder)
            }
        }
    }

    private fun highlightDynamic(
        expression: RsDynamicExpression,
        holder: AnnotationHolder,
    ) {
        when (val resolved = expression.typeCheckedResolvedSymbol) {
            is RsLocalVariableExpression -> {
                expression.highlight(holder, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
            }

            is RsScript -> {
                if (resolved.triggerName == "command") {
                    expression.highlight(holder, RsSyntaxHighlighterColors.COMMAND_CALL)
                }
            }

            is RsSymSymbol -> {
                expression.highlight(holder, RsSyntaxHighlighterColors.CONFIG_REFERENCE)
            }
        }
    }

    private fun PsiElement.highlight(
        holder: AnnotationHolder,
        attribute: TextAttributesKey,
    ) {
        textRange.highlight(holder, attribute)
    }

    private fun TextRange.highlight(
        holder: AnnotationHolder,
        attribute: TextAttributesKey,
    ) {
        holder
            .newSilentAnnotation(HighlightInfoType.SYMBOL_TYPE_SEVERITY)
            .range(this)
            .textAttributes(attribute)
            .create()
    }
}
