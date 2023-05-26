package io.runescript.plugin.ide.formatter

import io.runescript.plugin.ide.formatter.blocks.RsBlock
import io.runescript.plugin.lang.psi.RsArithmeticOp
import io.runescript.plugin.lang.psi.RsConditionOp
import io.runescript.plugin.lang.psi.RsElementTypes

fun RsBlock.isLogicalOperator(): Boolean {
    if (node.elementType != RsElementTypes.CONDITION_OP) {
        return false
    }
    val op = node.psi as RsConditionOp
    return op.bar != null || op.ampersand != null
}

fun RsBlock.isEqualityOperator(): Boolean {
    if (node.elementType != RsElementTypes.CONDITION_OP) {
        return false
    }
    val op = node.psi as RsConditionOp
    return op.equal != null || op.excel != null
}

fun RsBlock.isRelationalOperator(): Boolean {
    if (node.elementType != RsElementTypes.CONDITION_OP) {
        return false
    }
    val op = node.psi as RsConditionOp
    return op.lt != null || op.lte != null || op.gt != null || op.gte != null
}

fun RsBlock.isAdditiveOperator(): Boolean {
    if (node.elementType != RsElementTypes.ARITHMETIC_OP) {
        return false
    }
    val op = node.psi as RsArithmeticOp
    return op.plus != null || op.minus != null
}

fun RsBlock.isMultiplicativeOperator(): Boolean {
    if (node.elementType != RsElementTypes.ARITHMETIC_OP) {
        return false
    }
    val op = node.psi as RsArithmeticOp
    return op.star != null || op.slash != null || op.percent != null
}

fun RsBlock.isBitwiseOperator(): Boolean {
    if (node.elementType != RsElementTypes.ARITHMETIC_OP) {
        return false
    }
    val op = node.psi as RsArithmeticOp
    return op.ampersand != null || op.bar != null
}