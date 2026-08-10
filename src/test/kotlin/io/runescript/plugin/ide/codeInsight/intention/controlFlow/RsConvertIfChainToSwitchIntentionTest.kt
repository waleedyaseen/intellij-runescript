package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsConvertIfChainToSwitchIntentionTest : RsParserTestCase() {
    fun testConvertsIntegerChainAndElseToDefault() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (${'$'}value = 1) {
                        one();
                    } else if (${'$'}value = 2) {
                        two();
                    } else {
                        fallback();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertIfChainToSwitchIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        one();
                    case 2 :
                        two();
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testConvertsReversedBooleanComparisonsWithoutDefault() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (true = ${'$'}value) enabled();
                    else if (false = ${'$'}value) disabled();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertIfChainToSwitchIntention()

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_boolean (${'$'}value) {
                    case true :
                        enabled();
                    case false :
                        disabled();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testConvertsParenthesizedIntegerComparisons() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>if (((${'$'}value) = 1)) one();
                    else if ((${'$'}value = 0x2)) two();
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertIfChainToSwitchIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        one();
                    case 0x2 :
                        two();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testUnavailableForDifferentSelectors() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>if (${'$'}first = 1) one();
                else if (${'$'}second = 2) two();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForNonEqualityCondition() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>if (${'$'}value > 1) one();
                else if (${'$'}value > 2) two();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForNonSwitchableStringLiterals() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>if (${'$'}value = "one") one();
                else if (${'$'}value = "two") two();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForSelectorWithPossibleSideEffects() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>if (get_value() = 1) one();
                else if (get_value() = 2) two();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableWhenEquivalentIntegerLabelsWouldBeDuplicated() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>if (${'$'}value = 1) one();
                else if (${'$'}value = 0x1) two();
            }
            """.trimIndent(),
        )
    }

    private fun assertUnavailable(source: String) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsConvertIfChainToSwitchIntention().isAvailable(project, myFixture.editor, element))
    }

    private fun assertNoPsiErrors() {
        assertEmpty(PsiTreeUtil.findChildrenOfType(myFixture.file, PsiErrorElement::class.java))
    }
}
