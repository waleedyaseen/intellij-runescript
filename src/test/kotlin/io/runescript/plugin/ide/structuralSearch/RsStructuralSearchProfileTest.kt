package io.runescript.plugin.ide.structuralSearch

import com.intellij.psi.util.PsiTreeUtil
import com.intellij.structuralsearch.impl.matcher.PatternTreeContext
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScript

class RsStructuralSearchProfileTest : RsParserTestCase() {
    private val profile = RsStructuralSearchProfile()

    fun testRecognizesRuneScriptAndParsesStatementPattern() {
        assertTrue(profile.isMyLanguage(RuneScript))
        val pattern =
            profile.createPatternTree(
                "~${'$'}callee${'$'}();",
                PatternTreeContext.Block,
                RsFileType,
                RuneScript,
                null,
                project,
                false,
            )

        assertTrue(
            pattern.joinToString { "${it.javaClass.simpleName}:${it.text}" },
            pattern.any { it is RsGosubExpression || PsiTreeUtil.findChildOfType(it, RsGosubExpression::class.java) != null },
        )
    }

    fun testParsesWholeScriptPatternWithoutStatementWrapper() {
        val pattern =
            profile.createPatternTree(
                "[proc,${'$'}name${'$'}]\nreturn;",
                PatternTreeContext.File,
                RsFileType,
                RuneScript,
                null,
                project,
                false,
            )

        assertTrue(
            pattern.joinToString { "${it.javaClass.simpleName}:${it.text}" },
            pattern.any { it is RsScript || PsiTreeUtil.findChildOfType(it, RsScript::class.java) != null },
        )
    }
}
