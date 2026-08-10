package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsInvertIfConditionIntentionTest : RsParserTestCase() {
    fun testInvertsComparisonAndSwapsBranches() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value > 10) foo();
                    else bar();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsInvertIfConditionIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (${'$'}value <= 10) {
                    bar();
                } else {
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testAppliesDeMorganLaw() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}a = 1 & ${'$'}b ! 2) {
                        foo();
                    } else {
                        bar();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsInvertIfConditionIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if ((${'$'}a ! 1) | (${'$'}b = 2)) {
                    bar();
                } else {
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWithoutElseBranch() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value = 10) foo();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsInvertIfConditionIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testInvertsParenthesizedCondition() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if ((${'$'}value = 10)) foo(); else bar();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsInvertIfConditionIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if ((${'$'}value ! 10)) {
                    bar();
                } else {
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForBareBooleanAndAwayFromIfKeyword() {
        val bareBoolean =
            myFixture.configureByText(
                "bare.cs2",
                """
                [proc,main]
                {
                    <caret>if (true) foo(); else bar();
                }
                """.trimIndent(),
            )
        assertFalse(
            RsInvertIfConditionIntention().isAvailable(
                project,
                myFixture.editor,
                bareBoolean.findElementAt(myFixture.caretOffset)!!,
            ),
        )

        val awayFromKeyword =
            myFixture.configureByText(
                "condition.cs2",
                """
                [proc,main]
                {
                    if <caret>(${'$'}value = 1) foo(); else bar();
                }
                """.trimIndent(),
            )
        assertFalse(
            RsInvertIfConditionIntention().isAvailable(
                project,
                myFixture.editor,
                awayFromKeyword.findElementAt(myFixture.caretOffset)!!,
            ),
        )
    }
}
