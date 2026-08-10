package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsElseRemoverTest : RsParserTestCase() {
    fun testRemovesElseAndKeepsIf() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if (true) {
                        foo();
                    } else {
                        <caret>bar();
                    }
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidate =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .single { it.second is RsElseRemover }

        WriteCommandAction.runWriteCommandAction(project) {
            candidate.second.unwrap(myFixture.editor, candidate.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (true) {
                    foo();
                }
            }
            """.trimIndent(),
        )
    }

    fun testRemovesElseIfChain() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if (<caret>true) foo();
                    else if (false) bar();
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidate =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .single { it.second is RsElseRemover }

        WriteCommandAction.runWriteCommandAction(project) {
            candidate.second.unwrap(myFixture.editor, candidate.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (true) foo();
            }
            """.trimIndent(),
        )
    }
}
