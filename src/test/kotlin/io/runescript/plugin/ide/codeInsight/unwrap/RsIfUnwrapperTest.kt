package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsIfUnwrapperTest : RsParserTestCase() {
    fun testUnwrapsIfAndKeepsThenBranch() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if (<caret>true) {
                        foo();
                        bar();
                    }
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidate =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .single { it.second is RsIfUnwrapper }

        WriteCommandAction.runWriteCommandAction(project) {
            candidate.second.unwrap(myFixture.editor, candidate.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                foo();
                bar();
            }
            """.trimIndent(),
        )
    }

    fun testUnwrapsSingleStatementIf() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    if (<caret>true) foo();
                }
                """.trimIndent(),
            )
        val descriptor = RsUnwrapDescriptor()
        val candidate =
            descriptor
                .collectUnwrappers(project, myFixture.editor, file)
                .single { it.second is RsIfUnwrapper }

        WriteCommandAction.runWriteCommandAction(project) {
            candidate.second.unwrap(myFixture.editor, candidate.first)
        }

        myFixture.checkResult(
            """
            [proc,main]
            {
                foo();
            }
            """.trimIndent(),
        )
    }
}
