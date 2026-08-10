package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsRemoveBracesIntentionTest : RsParserTestCase() {
    fun testRemovesBracesFromIfBody() {
        checkIntention(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) <caret>{
                    foo();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}enabled = true) foo();
            }
            """.trimIndent(),
        )
    }

    fun testRemovesBracesFromElseBody() {
        checkIntention(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) foo();
                else <caret>{
                    bar();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}enabled = true) foo();
                else bar();
            }
            """.trimIndent(),
        )
    }

    fun testRemovesBracesFromWhileBody() {
        checkIntention(
            """
            [proc,main]
            {
                while (${'$'}running = true) <caret>{
                    tick();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                while (${'$'}running = true) tick();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForMultipleStatements() {
        assertUnavailable(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) <caret>{
                    foo();
                    bar();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForScopedVariableDeclaration() {
        assertUnavailable(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) <caret>{
                    def_int ${'$'}value = 1;
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenRemovalWouldCreateDanglingElse() {
        assertUnavailable(
            """
            [proc,main]
            {
                if (${'$'}outer = true) <caret>{
                    if (${'$'}inner = true) foo();
                } else bar();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenBlockCommentWouldBeLost() {
        assertUnavailable(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) <caret>{
                    // Keep this explanation.
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    private fun checkIntention(
        before: String,
        after: String,
    ) {
        val file = myFixture.configureByText("main.cs2", before)
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsRemoveBracesIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(after)
    }

    private fun assertUnavailable(source: String) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsRemoveBracesIntention().isAvailable(project, myFixture.editor, element))
    }
}
