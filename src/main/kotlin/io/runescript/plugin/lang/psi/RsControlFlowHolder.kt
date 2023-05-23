package io.runescript.plugin.lang.psi

import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.codeInsight.controlFlow.RsControlFlow
import io.runescript.plugin.ide.codeInsight.controlFlow.lvt.RsLvtAnalysis

interface RsControlFlowHolder : PsiElement {
    val controlFlow: RsControlFlow
    val lvtAnalysis: RsLvtAnalysis
}