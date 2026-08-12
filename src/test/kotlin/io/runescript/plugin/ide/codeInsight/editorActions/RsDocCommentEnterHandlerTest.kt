package io.runescript.plugin.ide.codeInsight.editorActions

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiDocumentManager
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.parser.RsParserTestCase

class RsDocCommentEnterHandlerTest : RsParserTestCase() {
    fun testGeneratesParameterAndReturnTags() {
        myFixture.configureByText(
            "main.cs2",
            """
            /**<caret>
            [proc,main](int ${'$'}value, string ${'$'}name)(int,string)
            return(${'$'}value, ${'$'}name);
            """.trimIndent(),
        )

        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_ENTER)

        myFixture.checkResult(
            """
            /**
             * <caret>
             *
             * @param ${'$'}value
             * @param ${'$'}name
             * @return
             * @return
             */
            [proc,main](int ${'$'}value, string ${'$'}name)(int,string)
            return(${'$'}value, ${'$'}name);
            """.trimIndent(),
        )
    }

    fun testPreservesIndentation() {
        myFixture.configureByText(
            "main.cs2",
            "    /**<caret>\n    [proc,indented]\n    return;",
        )

        val offset = myFixture.editor.caretModel.offset
        assertSame(RuneScript, myFixture.file.language)
        assertEquals(
            "/**",
            myFixture.editor.document.text
                .substring(offset - 3, offset),
        )
        var result: EnterHandlerDelegate.Result? = null
        WriteCommandAction.runWriteCommandAction(project) {
            result =
                RsDocCommentEnterHandler().preprocessEnter(
                    myFixture.file,
                    myFixture.editor,
                    Ref.create(offset),
                    Ref.create(0),
                    SimpleDataContext.getProjectContext(project),
                    null,
                )
            PsiDocumentManager.getInstance(project).commitDocument(myFixture.editor.document)
        }
        assertEquals(EnterHandlerDelegate.Result.Stop, result)

        assertTrue(
            myFixture.file.text,
            myFixture.file.text.contains("    /**\n     * \n     */\n    [proc,indented]"),
        )
    }

    fun testDoesNotGenerateWithoutFollowingScript() {
        myFixture.configureByText("main.cs2", "/**<caret> ordinary")

        myFixture.performEditorAction(IdeActions.ACTION_EDITOR_ENTER)

        assertFalse(myFixture.file.text.contains(" * @"))
    }
}
