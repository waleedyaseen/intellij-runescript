package io.runescript.plugin.ide.codeInsight.surroundWith

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsWithIfSurrounderTest : RsParserTestCase() {
    fun testSurroundsSelectedStatementsAndSelectsCondition() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <selection>foo();
                    bar();</selection>
                }
                """.trimIndent(),
            )
        val descriptor = RsStatementsSurroundDescriptor()
        val elements =
            descriptor.getElementsToSurround(
                file,
                myFixture.editor.selectionModel.selectionStart,
                myFixture.editor.selectionModel.selectionEnd,
            )

        val conditionRange =
            WriteCommandAction.runWriteCommandAction<TextRange?>(project) {
                descriptor.surrounders.single().surroundElements(project, myFixture.editor, elements)
            }

        myFixture.checkResult(
            """
            [proc,main]
            {
                if (true) {
                    foo();
                    bar();
                }
            }
            """.trimIndent(),
        )
        assertEquals("true", conditionRange?.substring(file.text))
    }

    fun testRejectsStatementsFromDifferentBlocks() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    <selection>foo();
                    if (true) {
                        bar();</selection>
                    }
                }
                """.trimIndent(),
            )
        val descriptor = RsStatementsSurroundDescriptor()

        val elements =
            descriptor.getElementsToSurround(
                file,
                myFixture.editor.selectionModel.selectionStart,
                myFixture.editor.selectionModel.selectionEnd,
            )

        assertEmpty(elements)
    }
}
