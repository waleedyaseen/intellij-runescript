package io.runescript.plugin.lang.psi

interface RsBinaryExpression : RsExpression {
    val left: RsExpression
    val right: RsExpression
}