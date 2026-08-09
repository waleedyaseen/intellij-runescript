package io.runescript.plugin.ide.refactoring

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStringLiteralContent

class RsScriptRenameTest : RsParserTestCase() {
    fun testRenamingProcUpdatesCallSite() {
        val target = addScript("target.cs2", "[proc,target]\n{\n}")
        val usage = configure("~target();")

        myFixture.renameElement(target, "renamed")

        assertTrue(usage.text.contains("~renamed();"))
    }

    fun testRenamingCommandUpdatesDocumentationLink() {
        val target = addScript("target.cs2", "[command,target]\n{\n}")
        val usage =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Calls [command/target]. */
                [proc,main]
                {
                }
                """.trimIndent(),
            )

        myFixture.renameElement(target, "renamed")

        assertTrue(usage.text.contains("[command/renamed]"))
    }

    fun testRenamingClientscriptUpdatesInjectedHook() {
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,set_hook](hook ${"$"}hook)
            {
            }
            """.trimIndent(),
        )
        val target = addScript("target.cs2", "[clientscript,target]\n{\n}")
        val usage = configure("set_hook(\"target\");")
        val host = PsiTreeUtil.findChildOfType(usage, RsStringLiteralContent::class.java)!!
        assertNotNull(InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(host))

        myFixture.renameElement(target, "renamed")

        assertTrue(usage.text.contains("set_hook(\"renamed\")"))
    }

    fun testRenamingClientscriptUpdatesSymbolMapping() {
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
        val target = addScript("scripts/target.cs2", "[clientscript,target]\n{\n}")
        val mapping = myFixture.addFileToProject("symbols/clientscript.sym", "1\t[clientscript,target]\n")

        myFixture.renameElement(target, "renamed")

        assertEquals("1\t[clientscript,renamed]\n", mapping.text)
    }

    private fun addScript(
        path: String,
        text: String,
    ): RsScript {
        val file = myFixture.addFileToProject(path, text)
        return PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!
    }

    private fun configure(expression: String) =
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                $expression
            }
            """.trimIndent(),
        )
}
