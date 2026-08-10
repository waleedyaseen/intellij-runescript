package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.openapi.command.WriteCommandAction
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsWhileUnwrapperTest : RsParserTestCase() {
    fun testUnwrapsWhileAndKeepsBody() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    while (<caret>true) {
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
                .single { it.second is RsWhileUnwrapper }

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
}
