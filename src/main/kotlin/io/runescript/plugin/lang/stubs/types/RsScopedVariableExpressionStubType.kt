package io.runescript.plugin.lang.stubs.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsStubType
import io.runescript.plugin.lang.psi.impl.RsScopedVariableExpressionImpl
import io.runescript.plugin.lang.stubs.RsScopedVariableExpressionStub

object RsScopedVariableExpressionStubType : RsStubType<RsScopedVariableExpressionStub, RsScopedVariableExpression>("SCOPED_VARIABLE_EXPRESSION") {

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RsScopedVariableExpressionStub {
        return RsScopedVariableExpressionStub(parentStub, this, dataStream.readUTF())
    }

    override fun serialize(stub: RsScopedVariableExpressionStub, dataStream: StubOutputStream) {
        dataStream.writeUTF(stub.name)
    }

    override fun createStub(psi: RsScopedVariableExpression, parentStub: StubElement<out PsiElement>?): RsScopedVariableExpressionStub {
        return RsScopedVariableExpressionStub(parentStub, this, psi.name!!)
    }

    override fun createPsi(stub: RsScopedVariableExpressionStub): RsScopedVariableExpression {
        return RsScopedVariableExpressionImpl(stub, this)
    }

    override fun indexStub(stub: RsScopedVariableExpressionStub, sink: IndexSink) {
        // TODO
    }
}