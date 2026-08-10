package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData

class RsDocCompletionProviderTest : BasePlatformTestCase() {
    fun testCompletesRSDocTagNamesWithoutCodeSuggestions() {
        complete(
            """
            /**
             * @pa<caret>
             */
            [proc,test](int ${"$"}value)
            {
            }
            """,
        )

        assertContains("@param", "@parammeta")
        assertDoesNotContain("if", "while", "def_int")
    }

    fun testCompletesOnlyUndocumentedParameterSubjects() {
        complete(
            """
            /**
             * @param first Existing documentation.
             * @param <caret>
             */
            [proc,test](int ${"$"}first, string ${"$"}second)
            {
            }
            """,
        )

        assertContains("second")
        assertDoesNotContain("first")
    }

    fun testCompletesApplicableParameterBehaviorsWithoutDuplicates() {
        complete(
            """
            /**
             * @parammeta colour rgb
             * @parammeta colour <caret>
             */
            [command,set_colour](int ${"$"}colour)
            {
            }
            """,
        )

        assertContains("argb", "constant")
        assertDoesNotContain("rgb")
    }

    fun testDoesNotSuggestIntegerColorBehaviorsForStringParameter() {
        complete(
            """
            /**
             * @parammeta text <caret>
             */
            [command,set_text](string ${"$"}text)
            {
            }
            """,
        )

        assertContains("constant")
        assertDoesNotContain("rgb", "argb")
    }

    fun testCompletesConstantBehaviorOptionsAndSkipsExistingValues() {
        addSymbolFixtures()
        complete(
            """
            /**
             * @parammeta alignment constant[align_left, <caret>]
             */
            [command,set_alignment](int ${"$"}alignment)
            {
            }
            """,
        )

        assertContains("align_right")
        assertDoesNotContain("align_left", "not_a_constant")
    }

    fun testConstantBehaviorCompletionInsertsOptionBrackets() {
        configure(
            """
            /**
             * @parammeta alignment con<caret>
             */
            [command,set_alignment](int ${"$"}alignment)
            {
            }
            """,
        )
        myFixture.complete(CompletionType.BASIC)
        if (!myFixture.file.text.contains("@parammeta alignment constant[]")) {
            selectLookup("constant")
            myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
        }

        assertTrue(myFixture.file.text.contains("@parammeta alignment constant[]"))
        assertEquals(myFixture.file.text.indexOf("constant[") + "constant[".length, myFixture.caretOffset)
    }

    private fun complete(text: String) {
        configure(text)
        myFixture.complete(CompletionType.BASIC)
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
        module.neptuneModuleData.updateFromImportData(
            NeptuneProjectImportData(
                name = "test",
                sourcePaths = emptyList(),
                symbolPaths = listOf("symbols"),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            ),
        )
    }

    private fun addSymbolFixtures() {
        myFixture.addFileToProject(
            "symbols/constant.sym",
            """
            align_left	int	0
            align_right	int	2
            """.trimIndent() + "\n",
        )
        myFixture.addFileToProject("symbols/obj.sym", "1\tnot_a_constant\n")
    }

    private fun selectLookup(lookupString: String) {
        val element =
            myFixture.lookupElements
                .orEmpty()
                .firstOrNull { lookupString in it.allLookupStrings }
                ?: error("Expected completion '$lookupString' in ${lookupStrings()}")
        myFixture.lookup.setCurrentItem(element)
    }

    private fun assertContains(vararg expected: String) {
        val values = lookupStrings()
        for (value in expected) {
            assertTrue("Expected completion '$value' in $values", value in values)
        }
    }

    private fun assertDoesNotContain(vararg unexpected: String) {
        val values = lookupStrings()
        for (value in unexpected) {
            assertFalse("Did not expect completion '$value' in $values", value in values)
        }
    }

    private fun lookupStrings(): Set<String> =
        myFixture.lookupElements
            .orEmpty()
            .flatMap { it.allLookupStrings }
            .toSet()
}
