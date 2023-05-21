package io.runescript.plugin.ide.inspections

import com.intellij.openapi.project.Project
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsScript

class RsScriptBuilder(private val trigger: String, private val name: String) {

    private val qualifiedName: String
        get() = "[$trigger,$name]"
    private val parameters = mutableListOf<RsScriptBuilderParameter>()
    private val returns = mutableListOf<String>()
    private val statements = mutableListOf<String>()

    fun build(project: Project): RsScript {
        return RsElementGenerator.createScript(project, buildText())
    }

    private fun buildText() = buildString {
        append(qualifiedName)
        if (parameters.isNotEmpty()) {
            append(parameters.joinToString(", ") { "${it.type} $${it.name}" })
        }
        if (returns.isNotEmpty()) {
            append(returns.joinToString(","))
        }
        appendLine()
        if (statements.isNotEmpty()) {
            statements.forEach { appendLine(it) }
        }
        appendLine()
    }

    fun statement(line: String): RsScriptBuilder {
        statements += line
        return this
    }
}

data class RsScriptBuilderParameter(val type: String, val name: String)
