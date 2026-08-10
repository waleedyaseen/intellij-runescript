package io.runescript.plugin.ide.refactoring

import com.intellij.psi.PsiDocumentManager
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData

class RsInlineLocalHandlerTest : BasePlatformTestCase() {
    fun testInlinesLiteralAtReferenceAndDeletesDeclaration() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}answer = 42;
                foo(${"$"}ans<caret>wer);
            }
            """,
        )

        inlineVariable()

        assertFile(
            """
            [proc,test]
            {
                foo(42);
            }
            """,
        )
    }

    fun testInlinesAllReferencesFromDeclaration() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}answer<caret> = 42;
                foo(${"$"}answer);
                bar(${"$"}answer);
            }
            """,
        )

        inlineVariable()

        assertFile(
            """
            [proc,test]
            {
                foo(42);
                bar(42);
            }
            """,
        )
    }

    fun testParenthesizesBinaryInitializer() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}answer = calc(40 + 2);
                foo(calc(${"$"}answer<caret> * 3));
            }
            """,
        )

        inlineVariable()

        assertTrue(myFixture.file.text, myFixture.file.text.contains("foo(calc(calc(40 + 2) * 3));"))
        assertFalse(myFixture.file.text.contains("def_int ${"$"}answer"))
    }

    fun testDoesNotInlineCallInitializer() {
        configure(
            """
            [proc,get_answer]()(int)
            {
                return(42);
            }

            [proc,test]
            {
                def_int ${"$"}answer = ~get_answer();
                foo(${"$"}answer<caret>);
            }
            """,
        )
        val before = myFixture.file.text

        inlineVariableExpectingUnavailable()

        assertEquals(before, myFixture.file.text)
    }

    fun testDoesNotInlineMutableLocalSnapshot() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}source = 1;
                def_int ${"$"}snapshot = ${"$"}source;
                ${"$"}source = 2;
                foo(${"$"}snapshot<caret>);
            }
            """,
        )
        val before = myFixture.file.text

        inlineVariableExpectingUnavailable()

        assertEquals(before, myFixture.file.text)
    }

    fun testDoesNotInlineVariableWithWriteUsage() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}answer = 42;
                ${"$"}answer<caret>++;
            }
            """,
        )
        val before = myFixture.file.text

        inlineVariableExpectingUnavailable()

        assertEquals(before, myFixture.file.text)
    }

    fun testDoesNotCrossScriptScopesWithSameVariableName() {
        configure(
            """
            [proc,first]
            {
                def_int ${"$"}value = 1;
                foo(${"$"}value<caret>);
            }

            [proc,second]
            {
                def_int ${"$"}value = 2;
                foo(${"$"}value);
            }
            """,
        )

        inlineVariable()

        assertTrue(myFixture.file.text.contains("[proc,first]\n{\n    foo(1);\n}"))
        assertTrue(myFixture.file.text.contains("def_int ${"$"}value = 2;"))
        assertTrue(myFixture.file.text.contains("foo(${"$"}value);"))
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
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

    private fun inlineVariable() {
        myFixture.performEditorAction("Inline")
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }

    private fun inlineVariableExpectingUnavailable() {
        try {
            inlineVariable()
        } catch (_: CommonRefactoringUtil.RefactoringErrorHintException) {
        }
    }

    private fun assertFile(expected: String) {
        assertEquals(expected.trimIndent(), myFixture.file.text.trim())
    }
}
