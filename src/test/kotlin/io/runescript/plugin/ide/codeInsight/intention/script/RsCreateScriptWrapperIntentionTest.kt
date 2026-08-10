package io.runescript.plugin.ide.codeInsight.intention.script

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.triggerName

class RsCreateScriptWrapperIntentionTest : RsParserTestCase() {
    fun testCreatesClientscriptWrapperForProcedureParameters() {
        configure(
            """
            [proc,<caret>draw](component ${'$'}com, intarray ${'$'}values)
            work(${'$'}com, ${'$'}values);
            """,
        )

        invoke()

        myFixture.checkResult(
            """
            [clientscript,draw](component ${'$'}com, intarray ${'$'}values)
            ~draw(${'$'}com, ${'$'}values);

            [proc,<caret>draw](component ${'$'}com, intarray ${'$'}values)
            work(${'$'}com, ${'$'}values);
            """.trimIndent(),
        )
        assertWrapperResolvesToProcedure()
    }

    fun testUsesDirectionSpecificActionText() {
        configure("[proc,<caret>draw]\nwork();")
        val createClientscript = RsCreateScriptWrapperIntention()
        assertEquals("Create forwarding script wrapper", createClientscript.text)
        assertTrue(createClientscript.isAvailable(project, myFixture.editor, caretElement()))
        assertEquals("Create clientscript wrapper", createClientscript.text)

        configure("[clientscript,<caret>draw]\nwork();")
        val extractProcedure = RsCreateScriptWrapperIntention()
        assertTrue(extractProcedure.isAvailable(project, myFixture.editor, caretElement()))
        assertEquals("Extract procedure and keep clientscript wrapper", extractProcedure.text)
    }

    fun testCreatesWrapperBeforeProcedureDocumentation() {
        configure(
            """
            /** Draws the interface. */
            [proc,<caret>draw]()
            work();
            """,
        )

        invoke()

        myFixture.checkResult(
            """
            [clientscript,draw]()
            ~draw();

            /** Draws the interface. */
            [proc,<caret>draw]()
            work();
            """.trimIndent(),
        )
    }

    fun testExtractsProcedureAndLeavesClientscriptWrapper() {
        configure(
            """
            /** Public entry point. */
            [clientscript,<caret>draw](component ${'$'}com, int ${'$'}index)
            if (${'$'}index > 0) {
                draw(${'$'}com);
            }
            """,
        )

        invoke()

        myFixture.checkResult(
            """
            /** Public entry point. */
            [clientscript,<caret>draw](component ${'$'}com, int ${'$'}index)
            ~draw(${'$'}com, ${'$'}index);

            [proc,draw](component ${'$'}com, int ${'$'}index)
            if (${'$'}index > 0) {
                draw(${'$'}com);
            }
            """.trimIndent(),
        )
        assertWrapperResolvesToProcedure()
    }

    fun testPreservesBracedBodyWhenExtractingProcedure() {
        configure(
            """
            [clientscript,<caret>draw]
            {
                work();
            }
            """,
        )

        invoke()

        myFixture.checkResult(
            """
            [clientscript,<caret>draw]
            ~draw();

            [proc,draw]
            {
                work();
            }
            """.trimIndent(),
        )
    }

    fun testUnavailableForProcedureWithReturns() {
        configure("[proc,<caret>value](int ${'$'}input)(int)\nreturn(${'$'}input);")

        assertFalse(isAvailable())
    }

    fun testUnavailableWhenCounterpartExistsInFile() {
        configure(
            """
            [clientscript,draw](int ${'$'}value)
            ~draw(${'$'}value);

            [proc,<caret>draw](int ${'$'}value)
            work(${'$'}value);
            """,
        )

        assertFalse(isAvailable())
    }

    fun testUnavailableWhenCounterpartExistsElsewhereInModule() {
        myFixture.addFileToProject("other.cs2", "[clientscript,draw](int ${'$'}value)\n~draw(${'$'}value);")
        configure("[proc,<caret>draw](int ${'$'}value)\nwork(${'$'}value);")

        assertFalse(isAvailable())
    }

    fun testUnavailableInsideScriptBody() {
        configure(
            """
            [proc,draw]
            <caret>work();
            """,
        )

        assertFalse(isAvailable())
    }

    fun testUnavailableForStarredScript() {
        configure("[proc,<caret>draw*]\nwork();")

        assertFalse(isAvailable())
    }

    fun testUnavailableForIncompleteParameter() {
        configure("[proc,draw](int <caret>)\nwork();")

        assertFalse(isAvailable())
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
    }

    private fun isAvailable(): Boolean = RsCreateScriptWrapperIntention().isAvailable(project, myFixture.editor, caretElement())

    private fun invoke() {
        val element = caretElement()
        val intention = RsCreateScriptWrapperIntention()
        assertTrue(intention.isAvailable(project, myFixture.editor, element))
        WriteCommandAction.runWriteCommandAction(project) {
            intention.invoke(project, myFixture.editor, element)
        }
    }

    private fun caretElement() = myFixture.file.findElementAt(myFixture.caretOffset)!!

    private fun assertWrapperResolvesToProcedure() {
        val scripts = PsiTreeUtil.getChildrenOfTypeAsList(myFixture.file, RsScript::class.java)
        val clientscript = scripts.single { it.triggerName == "clientscript" }
        val procedure = scripts.single { it.triggerName == "proc" }
        val call = PsiTreeUtil.findChildOfType(clientscript, RsGosubExpression::class.java)!!
        assertSame(procedure, call.reference!!.resolve())
    }
}
