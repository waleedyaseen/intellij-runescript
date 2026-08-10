package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsRemoveObsoleteDocTagsIntentionTest : RsParserTestCase() {
    fun testRemovesUnknownParametersAndExtraReturns() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * Does something.
                 *
                 * @param value Kept.
                 * @param stale Removed.
                 * @parammeta stale rgb
                 * @return Kept.
                 * @return Removed.
                 */
                [proc,<caret>main](int ${'$'}value)(int)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsRemoveObsoleteDocTagsIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * Does something.
             *
             * @param value Kept.
             * @return Kept.
             */
            [proc,main](int ${'$'}value)(int)
            {
            }
            """.trimIndent(),
        )
    }

    fun testKeepsMetadataForExistingParameter() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param value
                 * @parammeta value rgb
                 */
                [proc,<caret>main](int ${'$'}value)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsRemoveObsoleteDocTagsIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testRemovesContinuationLinesWithObsoleteTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /**
                 * @param stale Obsolete description
                 *   with a continuation.
                 * @return Obsolete result
                 *   with another continuation.
                 * @custom Kept.
                 */
                [proc,<caret>main]
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsRemoveObsoleteDocTagsIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * @custom Kept.
             */
            [proc,main]
            {
            }
            """.trimIndent(),
        )
    }
}
