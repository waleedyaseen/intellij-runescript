package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsConvertSwitchToIfChainIntentionTest : RsParserTestCase() {
    fun testConvertsMultiValueCasesAndDefault() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                        case 1, 2 :
                            low();
                        case 3 :
                            high();
                        case default :
                            fallback();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertSwitchToIfChainIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if ((${ '$' }value = 1) | (${ '$' }value = 2)) {
                    low();
                } else if ((${ '$' }value = 3)) {
                    high();
                } else {
                    fallback();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testConvertsWithoutDefault() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (%value) {
                        case 1 : one();
                        case 2 : two();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!

        WriteCommandAction.runWriteCommandAction(project) {
            RsConvertSwitchToIfChainIntention().invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if ((%value = 1)) {
                    one();
                } else if ((%value = 2)) {
                    two();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testConvertsSingleIntegerCaseWithDefault() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int (${'$'}value) {
                        case 1 : one();
                        case default : fallback();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertSwitchToIfChainIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if ((${ '$' }value = 1)) {
                    one();
                } else {
                    fallback();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testConvertsParenthesizedIntegerSelector() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <caret>switch_int ((${'$'}value)) {
                        case 1 : one();
                        case 0x2 : two();
                    }
                }
                """.trimIndent(),
            )
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsConvertSwitchToIfChainIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (((${'$'}value) = 1)) {
                    one();
                } else if (((${'$'}value) = 0x2)) {
                    two();
                }
            }
            """.trimIndent(),
        )
        assertNoPsiErrors()
    }

    fun testUnavailableWhenDefaultIsNotLast() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>switch_int (${'$'}value) {
                    case default : fallback();
                    case 1 : one();
                    case 2 : two();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForSelectorWithPossibleSideEffects() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>switch_int (get_value()) {
                    case 1 : one();
                    case 2 : two();
                }
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForDefaultOnlySwitch() {
        assertUnavailable(
            """
            [proc,main]
            {
                <caret>switch_int (${'$'}value) {
                    case default : fallback();
                }
            }
            """.trimIndent(),
        )
    }

    private fun assertUnavailable(source: String) {
        val file = myFixture.configureByText("main.cs2", source)
        val element = file.findElementAt(myFixture.caretOffset)!!

        assertFalse(RsConvertSwitchToIfChainIntention().isAvailable(project, myFixture.editor, element))
    }

    private fun assertNoPsiErrors() {
        assertEmpty(PsiTreeUtil.findChildrenOfType(myFixture.file, PsiErrorElement::class.java))
    }
}
