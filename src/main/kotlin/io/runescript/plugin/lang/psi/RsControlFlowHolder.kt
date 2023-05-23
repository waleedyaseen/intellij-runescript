package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.codeInsight.controlFlow.RsControlFlow

interface RsControlFlowHolder : PsiElement {
    val controlFlow: RsControlFlow
}