package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.codeInsight.isSafeToDiscard
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement

internal object RsLocalVariableDeletion {
    fun deletePreservingEffects(
        project: Project,
        declaration: RsLocalVariableDeclarationStatement,
    ) {
        val initializer = declaration.expressionList.getOrNull(1)
        if (initializer == null || initializer.isSafeToDiscard()) {
            declaration.delete()
        } else {
            declaration.replace(RsElementGenerator.createExpressionStatement(project, initializer.text))
        }
    }
}
