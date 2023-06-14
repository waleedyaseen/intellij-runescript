package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.lang.RuneScript
import org.jetbrains.annotations.NonNls

abstract class RsStubType<StubT : StubElement<*>, PsiT : PsiElement>(debugName: @NonNls String)
    : IStubElementType<StubT, PsiT>(debugName, RuneScript) {

    override fun getExternalId(): String {
        return "RuneScript.${super.toString()}"
    }
}