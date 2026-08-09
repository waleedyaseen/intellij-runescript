package io.runescript.plugin.ide.codeInsight

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsCommandExpression

class RsIncompleteCodeInsightTest : RsParserTestCase() {
    fun testIncompleteArrayAccessDoesNotBreakHighlighting() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_intarray ${"$"}values;
                    ${"$"}values(0);
                }
                """.trimIndent(),
            )
        removeArrayExpressions(file)

        myFixture.doHighlighting()
    }

    fun testIncompleteArrayArgumentDoesNotBreakParameterHints() {
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,use_value](int ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_intarray ${"$"}values;
                    use_value(${"$"}values(0));
                }
                """.trimIndent(),
            )
        val call = PsiTreeUtil.findChildOfType(file, RsCommandExpression::class.java)!!
        removeArrayExpressions(file)

        RsInlayParameterHintsProvider().getParameterHints(call)
    }

    private fun removeArrayExpressions(file: com.intellij.psi.PsiFile) {
        val arrayAccess = PsiTreeUtil.findChildOfType(file, RsArrayAccessExpression::class.java)!!
        WriteCommandAction.runWriteCommandAction(project) {
            arrayAccess.expressionList.reversed().forEach { it.delete() }
        }
        assertEmpty(arrayAccess.expressionList)
    }
}
