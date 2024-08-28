package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiElement
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

fun RsLocalVariableExpression.isForArrayDeclaration(): Boolean {
    // Either a parameter "intarray $array" or declaration "def_int $array(...)"
    val parent = parent
    if (parent is RsArrayVariableDeclarationStatement && this === parent.expressionList[0]) {
        return true
    }
    return parent is RsParameter && parent.arrayTypeLiteral != null
}

fun RsLocalVariableExpression.isForVariableDeclaration(): Boolean {
    // Either a parameter "int $array" or declaration "def_int $array"
    val parent = parent
    if (parent is RsLocalVariableDeclarationStatement && this === parent.expressionList[0]) {
        return true
    }
    return parent is RsParameter && parent.typeName != null
}

fun RsLocalVariableExpression.isForArrayAccess(): Boolean {
    val parent = parent
    return parent is RsArrayAccessExpression && this === parent.expressionList[0]
}

fun Project.isRsProject(): Boolean {
    val file = baseDir.findChild("neptune.toml")
    return file != null
}

fun PsiElement.isSourceFile(): Boolean {
    if (!project.isRsProject()) {
        return false
    }
    val containingFile = containingFile?.virtualFile ?: return false
    val fileIndex = ProjectFileIndex.getInstance(project)
    if (fileIndex.isExcluded(containingFile)) {
        return false
    }
    return fileIndex.isInContent(containingFile)
}