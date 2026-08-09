package io.runescript.plugin.lang.stubs.types

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsNameLiteral
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsNameLiteralImpl
import io.runescript.plugin.lang.stubs.RsNameLiteralStub

object RsNameLiteralStubType : RsStubType<RsNameLiteralStub, RsNameLiteral>("NAME_LITERAL") {
    override fun shouldCreateStub(node: ASTNode): Boolean {
        val parent = node.treeParent ?: return false
        if (parent.elementType == RsElementTypes.SCRIPT) return true
        return parent.elementType == RsElementTypes.LOCAL_VARIABLE_EXPRESSION &&
            parent.treeParent?.elementType == RsElementTypes.PARAMETER
    }

    override fun deserialize(
        dataStream: StubInputStream,
        parentStub: StubElement<*>?,
    ): RsNameLiteralStub = RsNameLiteralStub(parentStub, this)

    override fun serialize(
        stub: RsNameLiteralStub,
        dataStream: StubOutputStream,
    ) {
    }

    override fun createStub(
        psi: RsNameLiteral,
        parentStub: StubElement<out PsiElement>?,
    ): RsNameLiteralStub = RsNameLiteralStub(parentStub, this)

    override fun createPsi(stub: RsNameLiteralStub): RsNameLiteral = RsNameLiteralImpl(stub, this)

    override fun indexStub(
        stub: RsNameLiteralStub,
        sink: IndexSink,
    ) {
        // TODO
    }
}
