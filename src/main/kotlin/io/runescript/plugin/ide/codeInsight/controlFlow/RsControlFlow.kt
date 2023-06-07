package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlow
import com.intellij.codeInsight.controlflow.ControlFlowUtil
import com.intellij.codeInsight.controlflow.Instruction
import java.util.*

class RsControlFlow(private val instructions: Array<Instruction>) : ControlFlow {

    private val reachable = BitSet()

    init {
        ControlFlowUtil.process(this.instructions, 0) {
            reachable.set(it.num());
            true
        }
    }

    fun isReachable(instruction: Instruction): Boolean {
        require(instruction === instructions[instruction.num()])
        return reachable.get(instruction.num())
    }

    override fun getInstructions(): Array<Instruction> {
        return instructions
    }
}