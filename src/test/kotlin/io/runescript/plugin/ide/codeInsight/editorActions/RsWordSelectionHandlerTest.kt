package io.runescript.plugin.ide.codeInsight.editorActions

import io.runescript.plugin.lang.parser.RsParserTestCase

class RsWordSelectionHandlerTest : RsParserTestCase() {
    fun testExpandsThroughExpressionStatementBlockAndScript() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main](int ${'$'}value)
                {
                    debug(calc(${'$'}<caret>value + 1));
                }
                """.trimIndent(),
            )
        val caret = myFixture.editor.caretModel.offset
        val leaf = file.findElementAt(caret)!!
        val selectedText =
            RsWordSelectionHandler()
                .select(leaf, file.text, caret, myFixture.editor)
                .map { it.substring(file.text) }

        assertTrue(selectedText.any { it == "${'$'}value" })
        assertTrue(selectedText.any { it == "calc(${'$'}value + 1)" })
        assertTrue(selectedText.any { it == "debug(calc(${'$'}value + 1));" })
        assertTrue(selectedText.any { it.startsWith("{") && it.endsWith("}") })
        assertEquals(file.text, selectedText.last())
    }
}
