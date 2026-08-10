package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsDocumentationIntentionAvailabilityTest : RsParserTestCase() {
    fun testMaintenanceIntentionsAreUnavailableInsideBody() {
        assertUnavailableInBody(
            RsAddMissingDocTagsIntention(),
            """
            /** Empty. */
            [proc,main](int ${'$'}value)
            {
                <caret>work();
            }
            """.trimIndent(),
        )
        assertUnavailableInBody(
            RsRemoveObsoleteDocTagsIntention(),
            """
            /**
             * @param stale Old.
             */
            [proc,main]
            {
                <caret>work();
            }
            """.trimIndent(),
        )
        assertUnavailableInBody(
            RsReorderDocTagsIntention(),
            """
            /**
             * @param second Second.
             * @param first First.
             */
            [proc,main](int ${'$'}first, int ${'$'}second)
            {
                <caret>work();
            }
            """.trimIndent(),
        )
    }

    fun testMaintenanceIntentionsRemainAvailableOnSignature() {
        assertAvailableOnSignature(
            RsAddMissingDocTagsIntention(),
            """
            /** Empty. */
            [proc,<caret>main](int ${'$'}value)
            {
            }
            """.trimIndent(),
        )
        assertAvailableOnSignature(
            RsRemoveObsoleteDocTagsIntention(),
            """
            /**
             * @param stale Old.
             */
            [proc,<caret>main]
            {
            }
            """.trimIndent(),
        )
        assertAvailableOnSignature(
            RsReorderDocTagsIntention(),
            """
            /**
             * @param second Second.
             * @param first First.
             */
            [proc,<caret>main](int ${'$'}first, int ${'$'}second)
            {
            }
            """.trimIndent(),
        )
    }

    fun testIntentionsRejectWholeScriptElementFallback() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Empty. */
                [proc,main](int ${'$'}value)
                {
                    work();
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        listOf(
            RsAddMissingDocTagsIntention(),
            RsRemoveObsoleteDocTagsIntention(),
            RsReorderDocTagsIntention(),
        ).forEach { intention ->
            assertFalse(intention.isAvailable(project, myFixture.editor, script))
        }
    }

    private fun assertUnavailableInBody(
        intention: BaseElementAtCaretIntentionAction,
        source: String,
    ) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!
        assertFalse(intention.isAvailable(project, myFixture.editor, element))
    }

    private fun assertAvailableOnSignature(
        intention: BaseElementAtCaretIntentionAction,
        source: String,
    ) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!
        assertTrue(intention.isAvailable(project, myFixture.editor, element))
    }
}
