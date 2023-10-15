package io.runescript.plugin.lang.psi

/**
 * test
 */
interface RsBinaryExpression : RsExpression {
    val left: RsExpression
    val right: RsExpression
}