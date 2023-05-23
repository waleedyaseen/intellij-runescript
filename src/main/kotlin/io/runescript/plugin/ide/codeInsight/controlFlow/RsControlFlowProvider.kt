package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlow
import com.intellij.codeInsight.controlflow.ControlFlowProvider
import com.intellij.codeInsight.controlflow.Instruction
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.psi.RsControlFlowHolder

class RsControlFlowProvider : ControlFlowProvider{

    override fun getControlFlow(element: PsiElement): ControlFlow? {
        return element.parentOfType<RsControlFlowHolder>()?.controlFlow
    }

    override fun getAdditionalInfo(instruction: Instruction): String? {
        return null
    }
}