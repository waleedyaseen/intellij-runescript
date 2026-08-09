package io.runescript.plugin.ide.refactoring

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsCommandRenameTest : RsParserTestCase() {
    fun testRenamingCommandUpdatesCallSites() {
        val declarationFile =
            myFixture.addFileToProject(
                "commands.cs2",
                """
                [command,old_name]
                {
                }
                """.trimIndent(),
            )
        val usageFile =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    old_name();
                }
                """.trimIndent(),
            )
        val command = PsiTreeUtil.findChildOfType(declarationFile, RsScript::class.java)!!

        myFixture.renameElement(command, "new_name")

        assertEquals(
            """
            [proc,main]
            {
                new_name();
            }
            """.trimIndent(),
            usageFile.text,
        )
    }
}
