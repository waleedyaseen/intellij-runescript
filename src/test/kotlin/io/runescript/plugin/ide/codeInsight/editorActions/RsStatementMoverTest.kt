package io.runescript.plugin.ide.codeInsight.editorActions

import com.intellij.openapi.actionSystem.IdeActions
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsStatementMoverTest : RsParserTestCase() {
    fun testMovesStatementDown() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                <caret>first();
                second();
            }
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_DOWN_ACTION)

        myFixture.checkResult(
            """
            [proc,main]
            {
                second();
                <caret>first();
            }
            """.trimIndent(),
        )
    }

    fun testMovesMultilineStatementUpAsAUnit() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                before();
                <caret>if (${'$'}value = 1) {
                    first();
                    second();
                }
            }
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_UP_ACTION)

        myFixture.checkResult(
            """
            [proc,main]
            {
                <caret>if (${'$'}value = 1) {
                    first();
                    second();
                }
                before();
            }
            """.trimIndent(),
        )
    }

    fun testMovesWholeSwitchCase() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 : one();
                    <caret>case 2 :
                        first();
                        second();
                    case default : fallback();
                }
            }
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_UP_ACTION)

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 2 :
                        first();
                        second();
                    case 1 : one();
                    case default : fallback();
                }
            }
            """.trimIndent(),
        )
    }

    fun testDoesNotMoveCaseBelowDefault() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    <caret>case 1 : one();
                    case default : fallback();
                }
            }
            """.trimIndent(),
        )
        val original = myFixture.file.text

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_DOWN_ACTION)

        assertEquals(original, myFixture.file.text)
    }

    fun testMovesLeadingCommentWithStatement() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                first();
                // Explain the second call.
                <caret>second();
            }
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_UP_ACTION)

        myFixture.checkResult(
            """
            [proc,main]
            {
                // Explain the second call.
                <caret>second();
                first();
            }
            """.trimIndent(),
        )
    }

    fun testMovesLeadingCommentWithSwitchCase() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 : one();
                    // Explain the second case.
                    <caret>case 2 : two();
                }
            }
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_UP_ACTION)

        myFixture.checkResult(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    // Explain the second case.
                    <caret>case 2 : two();
                    case 1 : one();
                }
            }
            """.trimIndent(),
        )
    }

    fun testDoesNotFailInEmptyFile() {
        myFixture.configureByText("empty.cs2", "<caret>")

        myFixture.performEditorAction(IdeActions.ACTION_MOVE_STATEMENT_DOWN_ACTION)

        myFixture.checkResult("<caret>")
    }
}
