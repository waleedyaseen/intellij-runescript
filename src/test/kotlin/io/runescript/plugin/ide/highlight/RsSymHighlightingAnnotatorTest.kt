package io.runescript.plugin.ide.highlight

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsSymHighlightingAnnotatorTest : RsParserTestCase() {
    override fun setUp() {
        super.setUp()
        module.neptuneModuleData.updateFromImportData(
            NeptuneProjectImportData(
                name = "test",
                sourcePaths = listOf("scripts"),
                symbolPaths = listOf("symbols"),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            ),
        )
    }

    fun testHighlightsSymbolNameAsConfigDeclaration() {
        val file = myFixture.addFileToProject("symbols/obj.sym", "1\tmy_object\n")
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        val nameRange = PsiTreeUtil.findChildOfType(file, RsSymSymbol::class.java)!!.nameIdentifier!!.textRange

        val highlight =
            myFixture
                .doHighlighting()
                .single { info -> info.startOffset == nameRange.startOffset && info.endOffset == nameRange.endOffset }

        assertEquals(RsSyntaxHighlighterColors.CONFIG_REFERENCE, highlight.forcedTextAttributesKey)
    }
}
