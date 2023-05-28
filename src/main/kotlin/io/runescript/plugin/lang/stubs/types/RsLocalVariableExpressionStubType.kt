package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.*
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.impl.RsLocalVariableExpressionImpl
import io.runescript.plugin.lang.stubs.RsLocalVariableExpressionStub

object RsLocalVariableExpressionStubType : IStubElementType<RsLocalVariableExpressionStub, RsLocalVariableExpression>("LOCAL_VARIABLE_EXPRESSION", RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsLocalVariableExpressionStub {
        return RsLocalVariableExpressionStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsLocalVariableExpressionStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.name)
    }

    override fun createStub(psi: RsLocalVariableExpression, parentStub: StubElement<out PsiElement>?): RsLocalVariableExpressionStub {
        return RsLocalVariableExpressionStub(parentStub, this, psi.variableName)
    }

    override fun createPsi(stub: RsLocalVariableExpressionStub): RsLocalVariableExpression {
        return RsLocalVariableExpressionImpl(stub, this)
    }

    override fun indexStub(stub: RsLocalVariableExpressionStub, sink: IndexSink) {
        // TODO
    }
}