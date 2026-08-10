package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsAddBracesIntentionTest : RsParserTestCase() {
    fun testAddsBracesToIfBody() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>if (${'$'}enabled = true) foo();
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}enabled = true) {
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testAddsBracesToElseBody() {
        checkIntention(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) {
                    foo();
                } <caret>else bar();
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}enabled = true) {
                    foo();
                } else {
                    bar();
                }
            }
            """.trimIndent(),
        )
    }

    fun testAddsBracesToWhileBody() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>while (${'$'}running = true) tick();
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                while (${'$'}running = true) {
                    tick();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForBlockBody() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}enabled = true) {
                        foo();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsAddBracesIntention().isAvailable(project, myFixture.editor, element))
    }

    private fun checkIntention(
        before: String,
        after: String,
    ) {
        val file = myFixture.configureByText("main.cs2", before)
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddBracesIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(after)
    }
}
