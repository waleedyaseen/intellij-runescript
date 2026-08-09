package io.runescript.plugin.lang.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IFileElementType
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.RuneScriptHook
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes

object RsHookFileElementType : IFileElementType("RuneScript hook fragment", RuneScriptHook) {
    override fun doParseContents(
        chameleon: ASTNode,
        psi: PsiElement,
    ): ASTNode? {
        val builder =
            PsiBuilderFactory
                .getInstance()
                .createBuilder(
                    psi.project,
                    chameleon,
                    RsLexerAdapter(RsLexerInfo(psi.typeManager)),
                    getLanguageForParser(psi),
                    chameleon.chars,
                )
        return RsParser().parse(RsElementTypes.HOOK_ROOT, builder).firstChildNode
    }
}
