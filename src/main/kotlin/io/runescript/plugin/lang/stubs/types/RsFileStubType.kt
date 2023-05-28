package io.runescript.plugin.lang.stubs.types

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.psi.tree.IStubFileElementType
import io.runescript.plugin.ide.config.RsConfig
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.parser.RsParser
import io.runescript.plugin.lang.stubs.RsFileStub

object RsFileStubType : IStubFileElementType<RsFileStub>(RuneScript) {

    override fun doParseContents(chameleon: ASTNode, psi: PsiElement): ASTNode? {
        val project = psi.project
        val languageForParser = getLanguageForParser(psi)
        val lexer = RsLexerAdapter(RsLexerInfo(RsConfig.getPrimitiveTypes(project)))
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, lexer, languageForParser, chameleon.chars)
        val node = RsParser().parse(this, builder)
        return node.firstChildNode
    }

    override fun getStubVersion() = 0

    override fun serialize(stub: RsFileStub, dataStream: StubOutputStream) {

    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsFileStub {
        return RsFileStub(null)
    }

    override fun getExternalId() = "RuneScript.file"
}