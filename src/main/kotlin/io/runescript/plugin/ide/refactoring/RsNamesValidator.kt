package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.config.RsConfig

class RsNamesValidator : NamesValidator {
    private val keywords = hashSetOf<String>(
        "if",
        "else",
        "while",
        "case",
        "default",
        "return",
        "calc",
        "return",
        "true",
        "false",
        "null",
        *RsConfig.getPrimitiveTypes().map { "def_$it" }.toTypedArray(),
        *RsConfig.getPrimitiveTypes().map { "switch_$it" }.toTypedArray(),
        *RsConfig.getPrimitiveTypes().map { "${it}array" }.toTypedArray(),
    )

    override fun isKeyword(name: String, project: Project?): Boolean {
        return keywords.contains(name)
    }

    override fun isIdentifier(name: String, project: Project?): Boolean {
        return name.matches(Regex("[a-zA-Z0-9_+.:]+"))
    }
}