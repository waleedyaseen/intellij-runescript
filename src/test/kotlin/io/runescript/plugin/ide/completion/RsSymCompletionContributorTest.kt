package io.runescript.plugin.ide.completion

import com.intellij.psi.PsiManager
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsSymCompletionContributorTest : RsParserTestCase() {
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
        myFixture.addFileToProject(
            "scripts/clientscripts.cs2",
            """
            [clientscript,target_script]
            {
            }

            [clientscript,target_second]
            {
            }
            """.trimIndent(),
        )
    }

    fun testCompletesClientscriptNameInSymbolMapping() {
        val file = myFixture.addFileToProject("symbols/clientscript.sym", "1\t[clientscript,tar]\n")
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        myFixture.editor.caretModel.moveToOffset(file.text.indexOf(']'))
        assertContainsElements(StubIndex.getInstance().getAllKeys(RsClientScriptIndex.KEY, project), "target_script")

        val lookups = myFixture.completeBasic().map { it.lookupString }

        assertContainsElements(lookups, "target_script", "target_second")
    }

    fun testClientscriptMappingNavigatesToScript() {
        val targetFile = PsiManager.getInstance(project).findFile(myFixture.findFileInTempDir("scripts/clientscripts.cs2"))!!
        val file = myFixture.addFileToProject("symbols/clientscript.sym", "1\t[clientscript,target_script]\n")
        val symbol = PsiTreeUtil.findChildOfType(file, RsSymSymbol::class.java)!!
        val target = PsiTreeUtil.findChildOfType(targetFile, RsScript::class.java)

        assertSame(target, symbol.reference?.resolve())
    }
}
