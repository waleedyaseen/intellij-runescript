package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsAddMissingDocTagsIntentionTest : RsParserTestCase() {
    fun testAddsOnlyMissingTagsInSignatureOrder() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * Does something.
                 *
                 * @param count Existing description.
                 * @return Existing return.
                 */
                [proc,<caret>main](int ${'$'}count, string ${'$'}name)(int, string)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddMissingDocTagsIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * Does something.
             *
             * @param count Existing description.
             * @param name
             * @return Existing return.
             * @return
             */
            [proc,main](int ${'$'}count, string ${'$'}name)(int, string)
            {
            }
            """.trimIndent(),
        )
    }

    fun testExpandsSingleLineComment() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Does something. */
                [proc,<caret>main](int ${'$'}value)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddMissingDocTagsIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * Does something.
             *
             * @param value
             */
            [proc,main](int ${'$'}value)
            {
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenAllTagsExist() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param value
                 * @return
                 */
                [proc,<caret>main](int ${'$'}value)(int)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsAddMissingDocTagsIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testInsertsMissingTagsInSignatureOrderBeforeCustomTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param second Existing.
                 * @custom Keep last.
                 */
                [proc,<caret>main](int ${'$'}first, int ${'$'}second)(int)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsAddMissingDocTagsIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * @param first
             * @param second Existing.
             * @return
             * @custom Keep last.
             */
            [proc,main](int ${'$'}first, int ${'$'}second)(int)
            {
            }
            """.trimIndent(),
        )
    }
}
