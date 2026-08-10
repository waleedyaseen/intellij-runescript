package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsSplitIfAndIntentionTest : RsParserTestCase() {
    fun testSplitsAndConditionIntoNestedIfStatements() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}a = 1 & ${'$'}b = 2) {
                        foo();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsSplitIfAndIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}a = 1) {
                    if (${'$'}b = 2) {
                        foo();
                    }
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenIfHasElseBranch() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}a = 1 & ${'$'}b = 2) foo();
                    else bar();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsSplitIfAndIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testSplitsParenthesizedCondition() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if ((${'$'}a = 1 & ${'$'}b = 2)) foo();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsSplitIfAndIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}a = 1) {
                    if (${'$'}b = 2) foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableAwayFromIfKeyword() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if <caret>(${'$'}a = 1 & ${'$'}b = 2) foo();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsSplitIfAndIntention().isAvailable(project, myFixture.editor, element))
    }
}
