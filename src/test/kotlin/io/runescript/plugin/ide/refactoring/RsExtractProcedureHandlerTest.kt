package io.runescript.plugin.ide.refactoring

import com.intellij.codeInsight.template.impl.TemplateManagerImpl
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData

class RsExtractProcedureHandlerTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
        TemplateManagerImpl.setTemplateTesting(testRootDisposable)
        module.neptuneModuleData.updateFromImportData(
            NeptuneProjectImportData(
                name = "test",
                sourcePaths = emptyList(),
                symbolPaths = emptyList(),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            ),
        )
    }

    fun testExtractsStatementsAndDerivesInput() {
        configure(
            """
            [proc,main](int ${'$'}value)
            {
                <selection>debug(${'$'}value);
                work();</selection>
            }
            """,
        )

        extract()

        assertEquals(
            """
            [proc,main](int ${'$'}value)
            {
                ~extracted(${'$'}value);
            }

            [proc,extracted](int ${'$'}value)
            {
                debug(${'$'}value);
                work();
            }
            """.trimIndent(),
            myFixture.file.text.trim(),
        )
    }

    fun testOffersExtractScriptActionInsteadOfExtractMethod() {
        configure(
            """
            [proc,main]
            <selection>work();</selection>
            """,
        )

        assertEquals("Extract Script…", ActionManager.getInstance().getAction("RuneScript.ExtractScript").templateText)
        assertNull(RsRefactoringSupportProvider().extractMethodHandler)
    }

    fun testRenamesExtractedScriptInline() {
        configurePhysical(
            """
            [proc,main]
            <selection>work();</selection>
            """,
        )

        myFixture.performEditorAction("RuneScript.ExtractScript")
        assertNotNull(TemplateManagerImpl.getTemplateState(myFixture.editor))
        assertEquals("extracted", myFixture.editor.selectionModel.selectedText)
        myFixture.type("do_work")
        myFixture.performEditorAction("NextTemplateVariable")
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        assertTrue(myFixture.file.text, myFixture.file.text.contains("~do_work();"))
        assertTrue(myFixture.file.text, myFixture.file.text.contains("[proc,do_work]"))
    }

    fun testDerivesReturnedVariable() {
        configure(
            """
            [proc,main](int ${'$'}value)
            {
                def_int ${'$'}result = 0;
                <selection>${'$'}result = ${'$'}value;
                debug(${'$'}result);</selection>
                consume(${'$'}result);
            }
            """,
        )

        val extraction = RsExtractProcedureHandler().analyze(myFixture.editor, myFixture.file)!!
        assertEquals("${'$'}result = ~extracted(${'$'}value);", extraction.callText)
        extract()

        assertTrue(myFixture.file.text.contains("${'$'}result = ~extracted(${'$'}value);"))
        assertTrue(myFixture.file.text.contains("[proc,extracted](int ${'$'}value)(int)"))
        assertTrue(myFixture.file.text.contains("return(${'$'}result);"))
    }

    fun testDeclaresLocalReturnedFromSelectionAtCallSite() {
        configure(
            """
            [proc,main]
            {
                <selection>def_int ${'$'}result = 1;</selection>
                consume(${'$'}result);
            }
            """,
        )

        extract()

        assertTrue(myFixture.file.text.contains("def_int ${'$'}result;\n    ${'$'}result = ~extracted();"))
        assertTrue(myFixture.file.text.contains("[proc,extracted](int)"))
    }

    fun testRejectsPartialStatementsAndReturnFlow() {
        configure("[proc,main]\ndebug(<selection>1</selection>);")
        assertNull(RsExtractProcedureHandler().analyze(myFixture.editor, myFixture.file))

        configure("[proc,main]\n<selection>return;</selection>")
        assertNull(RsExtractProcedureHandler().analyze(myFixture.editor, myFixture.file))
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
    }

    private fun configurePhysical(text: String) {
        val markedText = text.trimIndent()
        val selectionStart = markedText.indexOf("<selection>")
        val selectionEnd = markedText.indexOf("</selection>")
        require(selectionStart >= 0 && selectionEnd >= selectionStart)
        val fileText = markedText.replace("<selection>", "").replace("</selection>", "")
        val file = myFixture.addFileToProject("main.cs2", fileText)
        myFixture.configureFromExistingVirtualFile(file.virtualFile)
        myFixture.editor.selectionModel.setSelection(selectionStart, selectionEnd - "<selection>".length)
    }

    private fun extract() {
        myFixture.performEditorAction("RuneScript.ExtractScript")
        TemplateManagerImpl.getTemplateState(myFixture.editor)?.gotoEnd()
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }
}
