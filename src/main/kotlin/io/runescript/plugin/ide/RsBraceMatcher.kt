package io.runescript.plugin.ide

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.psi.RsTypes

class RsBraceMatcher : PairedBraceMatcher {


    override fun getPairs(): Array<BracePair> {
        return PAIRS
    }

    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean {
        return true
    }

    override fun getCodeConstructStart(file: PsiFile?, openingBraceOffset: Int): Int {
        return openingBraceOffset
    }

    companion object {
        private val PAIRS = arrayOf(
            BracePair(RsTypes.LBRACE, RsTypes.RBRACE, true),
            BracePair(RsTypes.LPAREN, RsTypes.RPAREN, false),
            BracePair(RsTypes.LBRACKET, RsTypes.RBRACKET, false)
        )
    }
}