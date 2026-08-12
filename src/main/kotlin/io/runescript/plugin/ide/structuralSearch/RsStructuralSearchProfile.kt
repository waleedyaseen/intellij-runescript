package io.runescript.plugin.ide.structuralSearch

import com.intellij.codeInsight.template.EverywhereContextType
import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.structuralsearch.StructuralSearchProfileBase
import com.intellij.structuralsearch.impl.matcher.PatternTreeContext
import io.runescript.plugin.lang.RuneScript

class RsStructuralSearchProfile : StructuralSearchProfileBase() {
    override fun getVarPrefixes(): Array<String> = arrayOf(VARIABLE_PREFIX)

    override fun isMyLanguage(language: Language): Boolean = language == RuneScript

    override fun getContext(
        pattern: String,
        language: Language?,
        contextId: String?,
    ): String =
        if (pattern.trimStart().startsWith('[')) {
            PATTERN_PLACEHOLDER
        } else {
            "[proc,__structural_search__]\n{\n$PATTERN_PLACEHOLDER\n}"
        }

    override fun getTemplateContextTypeClass(): Class<out TemplateContextType> = EverywhereContextType::class.java

    override fun createPatternTree(
        text: String,
        context: PatternTreeContext,
        fileType: LanguageFileType,
        language: Language,
        contextId: String?,
        project: Project,
        physical: Boolean,
    ): Array<PsiElement> {
        val parseableText =
            VARIABLE_PATTERN.replace(text) { match ->
                VARIABLE_PREFIX + match.groupValues[1]
            }
        return super.createPatternTree(parseableText, context, fileType, language, contextId, project, physical)
    }

    private companion object {
        const val VARIABLE_PREFIX = "__rs_ssr_"
        val VARIABLE_PATTERN = """\$([A-Za-z_][A-Za-z0-9_]*)\$""".toRegex()
    }
}
