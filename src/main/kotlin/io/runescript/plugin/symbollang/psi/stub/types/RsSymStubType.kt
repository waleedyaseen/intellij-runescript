package io.runescript.plugin.symbollang.psi.stub.types

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.symbollang.RuneScriptSymbol
import org.jetbrains.annotations.NonNls

abstract class RsSymStubType<StubT : StubElement<*>, PsiT : PsiElement>(debugName: @NonNls String) :
    IStubElementType<StubT, PsiT>(debugName, RuneScriptSymbol) {
    override fun getExternalId(): String {
        return "RuneScriptSymbol.${super.toString()}"
    }
}