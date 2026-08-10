package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsElseUnwrapperTest : RsParserTestCase() {
    fun testUnwrapsElseAndKeepsElseBranch() {
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
                        baz();
                    }
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidate =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .single { it.second is RsElseUnwrapper }

        WriteCommandAction.runWriteCommandAction(project) {
            candidate.second.unwrap(myFixture.editor, candidate.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                bar();
                baz();
            }
            """.trimIndent(),
        )
    }

    fun testUnwrapsElseIfBranch() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if (true) foo();
                    else if (<caret>false) bar();
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidates =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .filter { it.second is RsElseUnwrapper }
        val outerIf = candidates.single { it.first.text.startsWith("if (true)") }

        WriteCommandAction.runWriteCommandAction(project) {
            outerIf.second.unwrap(myFixture.editor, outerIf.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (false) bar();
            }
            """.trimIndent(),
        )
    }
}
