package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsWithWhileSurrounder : RsStatementsSurrounder<RsWhileStatement>() {
    override fun getTemplateDescription(): String = RsBundle.message("surround.with.while.template")

    override fun createTemplate(
        project: Project,
        source: String,
    ): RsWhileStatement = RsElementGenerator.createStatement(project, "while (true) {\n$source\n}") as RsWhileStatement

    override fun getSelectionRange(statement: RsWhileStatement): TextRange? = statement.expression?.textRange
}
