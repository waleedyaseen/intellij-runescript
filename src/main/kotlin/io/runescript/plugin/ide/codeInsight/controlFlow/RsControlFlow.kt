package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlow
import com.intellij.codeInsight.controlflow.Instruction

class RsControlFlow(private val instructions: Array<Instruction>) : ControlFlow {

    override fun getInstructions(): Array<Instruction> {
        return instructions
    }
}