package io.runescript.plugin.ide.refactoring

import com.intellij.psi.PsiDocumentManager
import com.intellij.refactoring.util.CommonRefactoringUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData

class RsIntroduceVariableHandlerTest : BasePlatformTestCase() {
    fun testIntroducesSelectedExpressionBeforeOwningStatement() {
        configure(
            """
            [proc,test]
            {
                foo(<selection>42</selection>);
            }
            """,
        )

        introduceVariable()

        assertFileContains(
            """
            [proc,test]
            {
                def_int ${"$"}value = 42;
                foo(${"$"}value);
            }
            """,
        )
    }

    fun testUsesUniqueSuggestedName() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}value = 1;
                foo(<selection>42</selection>);
            }
            """,
        )

        introduceVariable()

        assertTrue(myFixture.file.text.contains("def_int ${"$"}value2 = 42;"))
        assertTrue(myFixture.file.text.contains("foo(${"$"}value2);"))
    }

    fun testSuggestsNameFromCall() {
        configure(
            """
            [proc,get_count]()(int)
            {
                return(1);
            }

            [proc,test]
            {
                def_int ${"$"}result = <selection>~get_count()</selection>;
            }
            """,
        )

        introduceVariable()

        assertTrue(myFixture.file.text.contains("def_int ${"$"}count = ~get_count();"))
        assertTrue(myFixture.file.text.contains("def_int ${"$"}result = ${"$"}count;"))
    }

    fun testIntroducesInsideBracedControlFlowBody() {
        configure(
            """
            [proc,test](int ${"$"}state)
            {
                if (${"$"}state = 1) {
                    foo(<selection>42</selection>);
                }
            }
            """,
        )

        introduceVariable()

        assertFileContains(
            """
            [proc,test](int ${"$"}state)
            {
                if (${"$"}state = 1) {
                    def_int ${"$"}value = 42;
                    foo(${"$"}value);
                }
            }
            """,
        )
    }

    fun testRejectsPartialExpressionSelection() {
        configure(
            """
            [proc,test]
            {
                foo(1<selection> + </selection>2);
            }
            """,
        )
        val before = myFixture.file.text

        introduceVariable()

        assertEquals(before, myFixture.file.text)
    }

    fun testRejectsAssignmentTargets() {
        configure(
            """
            [proc,test]
            {
                def_int ${"$"}value = 1;
                <selection>${"$"}value</selection> = 2;
            }
            """,
        )
        val before = myFixture.file.text

        introduceVariableExpectingRejection()

        assertEquals(before, myFixture.file.text)
    }

    fun testRejectsBraceLessControlFlowBody() {
        configure(
            """
            [proc,test](int ${"$"}state)
            {
                if (${"$"}state = 1)
                    foo(<selection>42</selection>);
            }
            """,
        )
        val before = myFixture.file.text

        introduceVariableExpectingRejection()

        assertEquals(before, myFixture.file.text)
    }

    fun testRejectsWhileCondition() {
        configure(
            """
            [proc,test](int ${"$"}state)
            {
                while (<selection>${"$"}state = 1</selection>) {
                }
            }
            """,
        )
        val before = myFixture.file.text

        introduceVariableExpectingRejection()

        assertEquals(before, myFixture.file.text)
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

    private fun introduceVariable() {
        myFixture.performEditorAction("IntroduceVariable")
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }

    private fun introduceVariableExpectingRejection() {
        try {
            introduceVariable()
            fail("Expected Introduce Variable to reject the selected expression")
        } catch (_: CommonRefactoringUtil.RefactoringErrorHintException) {
        }
    }

    private fun assertFileContains(expected: String) {
        assertEquals(expected.trimIndent(), myFixture.file.text.trim())
    }
}
