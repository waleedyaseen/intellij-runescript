package io.runescript.plugin.ide.refactoring

import com.intellij.lang.refactoring.NamesValidator
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import io.runescript.plugin.ide.neptune.DEFAULT_RESOLVED_DATA
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.typechecker.type.TypeManager

class RsNamesValidator : NamesValidator {
    override fun isKeyword(
        name: String,
        project: Project?,
    ): Boolean =
        name in KEYWORDS ||
            typeManagers(project).any { typeManager ->
                name in typeManager.typeKeywords ||
                    name in typeManager.defineKeywords ||
                    name in typeManager.switchKeywords
            }

    override fun isIdentifier(
        name: String,
        project: Project?,
    ): Boolean =
        name.isNotEmpty() &&
            typeManagers(project).all { typeManager ->
                val lexer = RsLexerAdapter(RsLexerInfo(typeManager))
                lexer.start(name)
                lexer.tokenType == RsElementTypes.IDENTIFIER && lexer.tokenEnd == name.length
            }

    private fun typeManagers(project: Project?): List<TypeManager> {
        if (project == null) {
            return listOf(DEFAULT_RESOLVED_DATA.types)
        }
        return ModuleManager
            .getInstance(project)
            .modules
            .map { module -> module.neptuneModuleData.resolvedData.types }
            .ifEmpty { listOf(DEFAULT_RESOLVED_DATA.types) }
    }

    private companion object {
        val KEYWORDS =
            setOf(
                "if",
                "else",
                "while",
                "case",
                "default",
                "return",
                "calc",
                "true",
                "false",
                "null",
            )
    }
}
