package io.runescript.plugin.lang.psi

interface RsCallExpression : RsExpression {
    val nameLiteral: RsNameLiteral?
    val argumentList: RsArgumentList?
}

val RsCallExpression.arguments: List<RsExpression>
    get() = argumentList?.expressionList ?: emptyList()