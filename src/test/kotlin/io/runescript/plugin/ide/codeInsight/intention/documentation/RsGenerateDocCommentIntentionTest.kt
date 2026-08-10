package io.runescript.plugin.ide.codeInsight.intention.documentation

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsGenerateDocCommentIntentionTest : RsParserTestCase() {
    fun testGeneratesParameterAndReturnTags() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,<caret>main](int ${'$'}count, string ${'$'}name)(int, string)
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsGenerateDocCommentIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * <caret>
             *
             * @param count
             * @param name
             * @return
             * @return
             */
            [proc,main](int ${'$'}count, string ${'$'}name)(int, string)
            {
            }
            """.trimIndent(),
        )
        assertNotNull(PsiTreeUtil.findChildOfType(file, RsScript::class.java)?.findDoc())
    }

    fun testGeneratesMinimalCommentForEmptySignature() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,<caret>main]
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsGenerateDocCommentIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            /**
             * <caret>
             */
            [proc,main]
            {
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenScriptAlreadyHasDocComment() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Existing. */
                [proc,<caret>main]
                {
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsGenerateDocCommentIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testUnavailableInsideScriptBody() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value = 1) {
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsGenerateDocCommentIntention().isAvailable(project, myFixture.editor, element))
    }

    fun testUnavailableForWholeScriptElementFallback() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    work();
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertFalse(RsGenerateDocCommentIntention().isAvailable(project, myFixture.editor, script))
    }
}
