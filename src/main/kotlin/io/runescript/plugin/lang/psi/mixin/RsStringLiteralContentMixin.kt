package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.ProperTextRange
import com.intellij.openapi.util.TextRange
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiLanguageInjectionHost
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsStringLiteralContent
import io.runescript.plugin.lang.psi.isBasicContent

abstract class RsStringLiteralContentMixin(node: ASTNode) : ASTWrapperPsiElement(node), RsStringLiteralContent {

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val stringLiteralContent = RsElementGenerator.createStringLiteralContent(project, text)
        return replace(stringLiteralContent) as RsStringLiteralContent
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> {
        return object : LiteralTextEscaper<RsStringLiteralContent>(this) {
            override fun decode(rangeInsideHost: TextRange, outChars: StringBuilder): Boolean {
                ProperTextRange.assertProperRange(rangeInsideHost)
                outChars.append(myHost.text, rangeInsideHost.startOffset, rangeInsideHost.endOffset)
                return true
            }

            override fun getOffsetInHost(offsetInDecoded: Int, rangeInsideHost: TextRange): Int {
                ProperTextRange.assertProperRange(rangeInsideHost)
                val offset = rangeInsideHost.startOffset + offsetInDecoded
                return offset.coerceIn(rangeInsideHost.startOffset..rangeInsideHost.endOffset)
            }

            override fun isOneLine(): Boolean {
                return true
            }
        }
    }

    override fun isValidHost(): Boolean {
        return isBasicContent()
    }
}