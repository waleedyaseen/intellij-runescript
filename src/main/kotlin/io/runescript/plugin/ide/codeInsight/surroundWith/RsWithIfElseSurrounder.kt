package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import io.runescript.plugin.ide.RsBundle
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIfStatement

class RsWithIfElseSurrounder : RsStatementsSurrounder<RsIfStatement>() {
    override fun getTemplateDescription(): String = RsBundle.message("surround.with.if.else.template")

    override fun createTemplate(
        project: Project,
        source: String,
    ): RsIfStatement = RsElementGenerator.createStatement(project, "if (true) {\n$source\n} else {}") as RsIfStatement

    override fun getSelectionRange(statement: RsIfStatement): TextRange? = statement.expression?.textRange
}
