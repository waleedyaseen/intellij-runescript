package io.runescript.plugin.lang.stubs.types

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import io.runescript.plugin.ide.neptune.typeManagerOrDefault
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.parser.RsParser
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.stubs.RsFileStub

object RsFileStubType : IStubFileElementType<RsFileStub>(RuneScript) {

    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val project = psi.project
        val languageForParser = getLanguageForParser(psi)
        val lexer = RsLexerAdapter(RsLexerInfo(project.typeManagerOrDefault))
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, lexer, languageForParser, chameleon.chars)
        val host = InjectedLanguageManager.getInstance(project).getInjectionHost(psi)
        val node = if (host != null) {
            RsParser().parse(RsElementTypes.HOOK_ROOT, builder)
        } else {
            RsParser().parse(this, builder)
        }
        return node.firstChildNode
    }

    override fun getStubVersion() = 4

    override fun serialize(stub: RsFileStub, dataStream: StubOutputStream) {

    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsFileStub {
        return RsFileStub(null)
    }

    override fun getExternalId() = "RuneScript.file"
}