package io.runescript.plugin.lang.psi

import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.neptune.findNeptuneProjectRoot

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

fun RsLocalVariableExpression.isForArrayDeclaration(): Boolean {
    // Either a parameter "intarray $array" or declaration "def_int $array(...)"
    val parent = parent
    if (parent is RsArrayVariableDeclarationStatement && this === parent.expressionList[0]) {
        return true
    }
    return parent is RsParameter && parent.typeName.text.endsWith("array")
}

fun RsLocalVariableExpression.isForVariableDeclaration(): Boolean {
    // Either a parameter "int $array" or declaration "def_int $array"
    val parent = parent
    if (parent is RsLocalVariableDeclarationStatement && this === parent.expressionList[0]) {
        return true
    }
    return parent is RsParameter && !parent.typeName.text.endsWith("array")
}

fun RsLocalVariableExpression.isForArrayAccess(): Boolean {
    val parent = parent
    return parent is RsArrayAccessExpression && this === parent.expressionList[0]
}

fun PsiElement.isSourceFile(): Boolean {
    val module = ModuleUtil.findModuleForFile(containingFile) ?: return false
    return module.findNeptuneProjectRoot() != null
}