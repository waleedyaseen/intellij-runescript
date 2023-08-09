package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlow
import com.intellij.codeInsight.controlflow.ControlFlowUtil
import com.intellij.codeInsight.controlflow.Instruction
import com.intellij.psi.PsiElement
import java.util.*

class RsControlFlow(private val instructions: Array<Instruction>) : ControlFlow {

    private val reachable = BitSet()
    private val reachableElements = HashSet<PsiElement>()

    init {
        ControlFlowUtil.process(this.instructions, 0) {
            val element = it.element
            if (element != null) {
                reachableElements.add(element)
            }
            reachable.set(it.num())
            true
        }
    }

    fun isReachable(element: PsiElement): Boolean {
        return reachableElements.contains(element)
    }

    fun isReachable(instruction: Instruction): Boolean {
        require(instruction === instructions[instruction.num()])
        return reachable.get(instruction.num())
    }

    override fun getInstructions(): Array<Instruction> {
        return instructions
    }
}