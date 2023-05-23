package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlowBuilder
import com.intellij.codeInsight.controlflow.impl.InstructionImpl
import com.intellij.psi.PsiElement

open class RsInstruction(builder: ControlFlowBuilder, element: PsiElement?) : InstructionImpl(builder, element)