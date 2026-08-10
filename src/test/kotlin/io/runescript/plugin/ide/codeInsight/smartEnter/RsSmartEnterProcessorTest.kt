package io.runescript.plugin.ide.codeInsight.smartEnter

import com.intellij.openapi.actionSystem.IdeActions
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsSmartEnterProcessorTest : RsParserTestCase() {
    fun testCompletesSemicolon() {
        checkSmartEnter(
            """
            [proc,main]
            {
                foo()<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                foo();
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesCallParenthesisAndSemicolon() {
        checkSmartEnter(
            """
            [proc,main]
            {
                foo(1<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                foo(1);
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesControlFlowParenthesisAndBraces() {
        checkSmartEnter(
            """
            [proc,main]
            {
                if (${'$'}value = 1<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}value = 1) {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testCompletesMissingControlFlowBraces() {
        checkSmartEnter(
            """
            [proc,main]
            {
                while (${'$'}running = true)<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                while (${'$'}running = true) {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testCompletesSwitchCaseColon() {
        checkSmartEnter(
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1<caret>
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                switch_int (${'$'}value) {
                    case 1 :
                        <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testCompletesInlineControlFlowBodyInsteadOfAddingBraces() {
        checkSmartEnter(
            """
            [proc,main]
            {
                if (${'$'}enabled = true) enable()<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}enabled = true) enable();
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testAddsBodyToScriptHeader() {
        checkSmartEnter(
            """
            [proc,main]<caret>
            """.trimIndent(),
            """
            [proc,main]
            {
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesScriptParameterListAndAddsBody() {
        checkSmartEnter(
            """
            [proc,main](int ${'$'}value<caret>
            """.trimIndent(),
            """
            [proc,main](int ${'$'}value)
            {
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesScriptReturnListAndAddsBody() {
        checkSmartEnter(
            """
            [proc,main](int ${'$'}value)(int<caret>
            """.trimIndent(),
            """
            [proc,main](int ${'$'}value)(int)
            {
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesNestedArgumentLists() {
        checkSmartEnter(
            """
            [proc,main]
            {
                outer(inner(1, value(2<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                outer(inner(1, value(2)));
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testCompletesReturnExpression() {
        checkSmartEnter(
            """
            [proc,main]
            {
                return(value(1<caret>
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                return(value(1));
                <caret>
            }
            """.trimIndent(),
        )
    }

    fun testUsesExistingControlFlowBlockOnFollowingLine() {
        checkSmartEnter(
            """
            [proc,main]
            {
                if (${'$'}value = 1<caret>
                {
                }
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                if (${'$'}value = 1)
                {
                    <caret>
                }
            }
            """.trimIndent(),
        )
    }

    fun testUsesExistingScriptBlockOnFollowingLine() {
        checkSmartEnter(
            """
            [proc,main]<caret>
            {
            }
            """.trimIndent(),
            """
            [proc,main]
            {
                <caret>
            }
            """.trimIndent(),
        )
    }

    private fun checkSmartEnter(
        before: String,
        after: String,
    ) {
        myFixture.configureByText("main.cs2", before)
        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT)
        myFixture.checkResult(after)
    }
}
