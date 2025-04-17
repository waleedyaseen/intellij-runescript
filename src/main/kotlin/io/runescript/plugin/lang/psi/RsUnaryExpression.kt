package io.runescript.plugin.lang.psi

interface RsUnaryExpression : RsExpression {
    val expression: RsExpression
    val unaryOp: RsUnaryOp
}