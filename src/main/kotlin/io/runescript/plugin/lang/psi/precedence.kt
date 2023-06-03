package io.runescript.plugin.lang.psi

const val PRECEDENCE_OTHER = 0
const val PRECEDENCE_RELATIONAL = 1
const val PRECEDENCE_EQUALITY = 2
const val PRECEDENCE_LOGICAL_AND = 3
const val PRECEDENCE_LOGICAL_OR = 4

val RsExpression.precedence: Int
    get() = when (this) {
        is RsConditionExpression -> when {
            conditionOp.isLogicalOr() -> PRECEDENCE_LOGICAL_OR
            conditionOp.isLogicalAnd() -> PRECEDENCE_LOGICAL_AND
            conditionOp.isEquality() -> PRECEDENCE_EQUALITY
            conditionOp.isRelational() -> PRECEDENCE_RELATIONAL
            else -> error("Unrecognized operator of conditional expression: $conditionOp")
        }

        else -> PRECEDENCE_OTHER
    }