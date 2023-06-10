package io.runescript.plugin.lang.psi

import com.intellij.psi.util.parentOfType

val RsStatement.controlFlowHolder: RsControlFlowHolder?
    get() = parentOfType<RsControlFlowHolder>()

fun RsConditionOp.isLogicalAnd() = ampersand != null
fun RsConditionOp.isLogicalOr() = bar != null
fun RsConditionOp.isEquality() = excel != null || equal != null
fun RsConditionOp.isRelational() = lt != null || lte != null || gt != null || gte != null

fun RsStatement?.isNullOrEmpty(): Boolean {
    if (this == null) {
        return true
    }
    if (this !is RsBlockStatement) {
        return false
    }
    return statementList.statementList.isEmpty()
}

fun RsStatement?.toSingleStatement(): RsStatement? {
    if (this == null || this !is RsBlockStatement) {
        return this
    }
    if (statementList.statementList.size != 1) {
        return null
    }
    return statementList.statementList[0]
}

val RsScript.qualifiedName: String
    get() = "[$triggerName,$scriptName]"

val RsScript.triggerNameExpression: RsNameLiteral
    get() = nameLiteralList[0]

val RsScript.triggerName: String
    get() = triggerNameExpression.text

val RsScript.scriptNameExpression: RsNameLiteral
    get() = nameLiteralList[1]

val RsScript.scriptName: String
    get() = scriptNameExpression.text

