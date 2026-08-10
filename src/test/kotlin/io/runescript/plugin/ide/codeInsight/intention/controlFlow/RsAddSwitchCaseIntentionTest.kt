package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsAddSwitchCaseIntentionTest : RsParserTestCase() {
    fun testAddsCaseAtEndAndSelectsValue() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>switch_int (${'$'}value) {
                    case 1 :
                        foo();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        foo();
                    case <selection>0</selection> :
            <body-indent>
                }
            }
            """.trimIndent().replace("<body-indent>", "            "),
        )
    }

    fun testAddsCaseBeforeDefault() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>switch_int (${'$'}value) {
                    case default :
                        fallback();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case <selection>0</selection> :
            <body-indent>
                    case default :
                        fallback();
                }
            }
            """.trimIndent().replace("<body-indent>", "            "),
        )
    }

    fun testChoosesUnusedIntegerPlaceholder() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>switch_int (${'$'}value) {
                    case 0 : zero();
                    case 1 : one();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 0 :
                        zero();
                    case 1 :
                        one();
                    case <selection>2</selection> :
            <body-indent>
                }
            }
            """.trimIndent().replace("<body-indent>", "            "),
        )
    }

    fun testUsesBooleanPlaceholderAndIgnoresDefaultTextInComment() {
        checkIntention(
            """
            [proc,main]
            {
                <caret>switch_boolean (${'$'}value) {
                    // case default : is documentation, not a label.
                    case false : disabled();
                    case default : fallback();
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_boolean (${'$'}value) {
                    // case default : is documentation, not a label.
                    case false :
                        disabled();
                    case <selection>true</selection> :
            <body-indent>
                    case default :
                        fallback();
                }
            }
            """.trimIndent().replace("<body-indent>", "            "),
        )
    }

    private fun checkIntention(
        before: String,
        after: String,
    ) {
        val file = myFixture.configureByText("main.cs2", before)
        val element = file.findElementAt(myFixture.caretOffset)!!
        val intention = RsAddSwitchCaseIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))

        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }

        myFixture.checkResult(after)
    }
}
