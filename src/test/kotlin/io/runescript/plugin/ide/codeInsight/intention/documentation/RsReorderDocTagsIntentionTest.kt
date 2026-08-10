package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsReorderDocTagsIntentionTest : RsParserTestCase() {
    fun testOrdersParametersReturnsAndPreservesOtherTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * Does work.
                 *
                 * @return Result description.
                 * @param second Second description
                 *   continues here.
                 * @custom retained
                 * @param first First description.
                 */
                [proc,<caret>main](int ${'$'}first, string ${'$'}second)(int)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsReorderDocTagsIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * Does work.
             *
             * @param first First description.
             * @param second Second description
             *   continues here.
             * @return Result description.
             * @custom retained
             */
            [proc,main](int ${'$'}first, string ${'$'}second)(int)
            {
            }
            """.trimIndent(),
        )
    }

    fun testOrdersParamMetaWithItsParameter() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @parammeta second Metadata.
                 * @param first First.
                 */
                [proc,<caret>main](int ${'$'}first, int ${'$'}second)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsReorderDocTagsIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * @param first First.
             * @parammeta second Metadata.
             */
            [proc,main](int ${'$'}first, int ${'$'}second)
            {
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenAlreadyOrdered() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param first First.
                 * @param second Second.
                 * @return Result.
                 * @custom retained
                 */
                [proc,<caret>main](int ${'$'}first, int ${'$'}second)(int)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsReorderDocTagsIntention().isAvailable(project, myFixture.editor, element))
    }
}
