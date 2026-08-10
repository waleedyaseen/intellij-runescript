package io.runescript.plugin.ide.refactoring

import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.ui.EditorTextField
import com.intellij.ui.table.JBTable
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scriptName
import java.awt.Container

class RsChangeSignatureProcessorTest : BasePlatformTestCase() {
    fun testAddsParameterAndDefaultArgumentToEveryCall() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
            }

            [proc,caller]
            {
                ~target(1);
                ~target(2);
            }
            """,
        )

        applyChange("int ${"$"}value\nstring ${"$"}caption = \"default\"")

        assertFileContains("[proc,target](int ${"$"}value, string ${"$"}caption)")
        assertFileContains("~target(1, \"default\");")
        assertFileContains("~target(2, \"default\");")
    }

    fun testUpdatesCallsInOtherFilesAndAddsMissingArgumentList() {
        configure(
            """
            [proc,target]
            {
            }
            """,
        )
        val caller =
            myFixture.addFileToProject(
                "caller.cs2",
                """
                [proc,caller]
                {
                    ~target;
                }
                """.trimIndent(),
            )

        applyChange("int ${"$"}value = 7")

        assertFileContains("[proc,target](int ${"$"}value)")
        assertTrue(caller.text, caller.text.contains("~target(7);"))
    }

    fun testReordersParametersAndCallArgumentsByIdentity() {
        configure(
            """
            [proc,target](int ${"$"}value, string ${"$"}caption)
            {
            }

            [proc,caller]
            {
                ~target(1, "hello");
            }
            """,
        )

        applyChange("string ${"$"}caption\nint ${"$"}value")

        assertFileContains("[proc,target](string ${"$"}caption, int ${"$"}value)")
        assertFileContains("~target(\"hello\", 1);")
    }

    fun testParameterTableRetainsIdentityWhenRowsMoveAndNamesChange() {
        configure(
            """
            [proc,target](int ${"$"}value, string ${"$"}caption)
            {
                foo(${"$"}value, ${"$"}caption);
            }

            [proc,caller]
            {
                ~target(1, "hello");
            }
            """,
        )
        val model = RsSignatureTableModel(targetScript())

        model.moveParameter(1, 0)
        model.setValueAt("title", 0, RsSignatureTableModel.NAME_COLUMN)
        val change = createChange(model.parameters())
        val error = RsChangeSignatureProcessor.apply(project, targetScript(), change)
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        assertNull(error)
        assertFileContains("[proc,target](string ${"$"}title, int ${"$"}value)")
        assertFileContains("foo(${"$"}value, ${"$"}title);")
        assertFileContains("~target(\"hello\", 1);")
    }

    fun testParameterTableAddsSafeUniqueParameterDefaults() {
        configure(
            """
            [proc,target](int ${"$"}parameter)
            {
            }

            [proc,caller]
            {
                ~target(1);
            }
            """,
        )
        val model = RsSignatureTableModel(targetScript())

        val newRow = model.addParameter()

        assertEquals(2, model.columnCount)
        assertEquals(1, newRow)
        assertEquals("int", model.getValueAt(newRow, RsSignatureTableModel.TYPE_COLUMN))
        assertEquals("${"$"}parameter2", model.getValueAt(newRow, RsSignatureTableModel.NAME_COLUMN))
        assertEquals("0", model.parameters()[newRow].newArgumentValue)
        val change = createChange(model.parameters())
        val error = RsChangeSignatureProcessor.apply(project, targetScript(), change)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        assertNull(error)
        assertFileContains("[proc,target](int ${"$"}parameter, int ${"$"}parameter2)")
        assertFileContains("~target(1, 0);")
    }

    fun testParameterTableDerivesNewCallArgumentFromChangedType() {
        configure(
            """
            [proc,target]
            {
            }
            """,
        )
        val model = RsSignatureTableModel(targetScript())
        val newRow = model.addParameter()

        model.setValueAt("string", newRow, RsSignatureTableModel.TYPE_COLUMN)

        assertEquals("\"\"", model.parameters().single().newArgumentValue)
    }

    fun testDialogUsesHighlightedSpaciousParameterListAndPreview() {
        configure(
            """
            [proc,target](component ${"$"}universe, component ${"$"}frame)
            {
            }
            """,
        )
        val dialog = RsChangeSignatureDialog(project, targetScript())
        try {
            val createCenterPanel = RsChangeSignatureDialog::class.java.getDeclaredMethod("createCenterPanel")
            createCenterPanel.isAccessible = true
            val centerPanel = createCenterPanel.invoke(dialog) as Container
            val table = findComponents(centerPanel, JBTable::class.java).single()
            val codeFields = findComponents(centerPanel, EditorTextField::class.java)

            assertEquals(2, table.columnCount)
            assertNull(table.tableHeader)
            assertFalse(table.showHorizontalLines)
            assertFalse(table.showVerticalLines)
            assertTrue(table.getCellRenderer(0, RsSignatureTableModel.TYPE_COLUMN) is ColoredTableCellRenderer)
            assertTrue(table.getCellRenderer(0, RsSignatureTableModel.NAME_COLUMN) is ColoredTableCellRenderer)
            assertTrue(centerPanel.preferredSize.width >= 700)
            assertTrue(codeFields.size >= 3)
            assertTrue(codeFields.any { it.preferredSize.height >= 100 })
        } finally {
            dialog.disposeIfNeeded()
        }
    }

    fun testRenamesParameterReferencesAndDocumentationSubjects() {
        configure(
            """
            /**
             * @param value old description
             * @parammeta value rgb
             */
            [proc,target](int ${"$"}value)
            {
                foo(${"$"}value);
            }

            [proc,caller]
            {
                ~target(1);
            }
            """,
        )

        applyChange("int ${"$"}shade")

        assertFileContains("@param shade old description")
        assertFileContains("@parammeta shade rgb")
        assertFileContains("[proc,target](int ${"$"}shade)")
        assertFileContains("foo(${"$"}shade);")
        assertFileContains("~target(1);")
        assertFalse(myFixture.file.text.contains("@param value"))
    }

    fun testRemovesUnusedParameterFromCallsAndDocumentation() {
        configure(
            """
            /**
             * @param first
             * @param second
             *   continuation
             */
            [proc,target](int ${"$"}first, int ${"$"}second)
            {
                foo(${"$"}first);
            }

            [proc,caller]
            {
                ~target(1, 2);
            }
            """,
        )

        applyChange("int ${"$"}first")

        assertFileContains("[proc,target](int ${"$"}first)")
        assertFileContains("~target(1);")
        assertFalse(myFixture.file.text.contains("@param second"))
        assertFalse(myFixture.file.text.contains("continuation"))
    }

    fun testChangesReturnTypesWithoutChangingArity() {
        configure(
            """
            [proc,target]()(int, string)
            {
                return(1, "one");
            }
            """,
        )

        applyChange("", "long, string")

        assertFileContains("[proc,target]()(long, string)")
    }

    fun testUpdatesRecursiveCallsAfterParameterRename() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
                ~target(${"$"}value);
            }
            """,
        )

        applyChange("int ${"$"}next")

        assertFileContains("[proc,target](int ${"$"}next)")
        assertFileContains("~target(${"$"}next);")
        assertFalse(myFixture.file.text.contains("${"$"}value"))
    }

    fun testRejectsRemovingParameterUsedByBody() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
                foo(${"$"}value);
            }
            """,
        )
        val before = myFixture.file.text
        val change = parseChange("")

        val error = RsChangeSignatureProcessor.apply(project, targetScript(), change)

        assertNotNull(error)
        assertTrue(error!!, error.contains("still used"))
        assertEquals(before, myFixture.file.text)
    }

    fun testRejectsIncompleteOrWrongArityCallWithoutEditing() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
            }

            [proc,caller]
            {
                ~target();
            }
            """,
        )
        val before = myFixture.file.text
        val change = parseChange("int ${"$"}value\nint ${"$"}extra = 2")

        val error = RsChangeSignatureProcessor.apply(project, targetScript(), change)

        assertNotNull(error)
        assertTrue(error!!, error.contains("expected 1"))
        assertEquals(before, myFixture.file.text)
    }

    fun testRejectsDuplicateInvalidAndUnsafeNewParameters() {
        configure("[proc,target](int ${"$"}value)\n{\n}")

        assertParseError("int ${"$"}value\nint ${"$"}value", "Duplicate parameter")
        assertParseError("not_a_type ${"$"}value", "not a valid RuneScript type")
        assertParseError("int ${"$"}value\nstring ${"$"}added", "requires a value for existing calls")
        assertParseError("int value =", "Invalid parameter")
        assertParseError("int ${"$"}value\nstring ${"$"}added = )", "not a valid RuneScript argument value")
        assertParseError("int ${"$"}value", returnText = "int, string", expected = "Adding or removing return values")
    }

    fun testHandlerTargetsOnlyContainingScript() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
                foo(${"$"}val<caret>ue);
            }
            """,
        )

        val target = RsChangeSignatureHandler().findTargetMember(myFixture.elementAtCaret)

        assertSame(targetScript(), target)
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
                longSupport = true,
            ),
        )
    }

    private fun targetScript(): RsScript =
        PsiTreeUtil.findChildrenOfType(myFixture.file, RsScript::class.java).first { script -> script.scriptName == "target" }

    private fun parseChange(
        parameterText: String,
        returnText: String = RsChangeSignatureProcessor.returnEditorText(targetScript()),
    ): RsSignatureChange =
        when (val result = RsChangeSignatureProcessor.parseChange(project, targetScript(), parameterText, returnText)) {
            is RsChangeSignatureProcessor.ParseResult.Error -> error(result.message)
            is RsChangeSignatureProcessor.ParseResult.Success -> result.change
        }

    private fun createChange(parameters: List<RsSignatureParameter>): RsSignatureChange =
        when (
            val result =
                RsChangeSignatureProcessor.createChange(
                    project,
                    targetScript(),
                    parameters,
                    RsChangeSignatureProcessor.returnEditorText(targetScript()),
                )
        ) {
            is RsChangeSignatureProcessor.ParseResult.Error -> error(result.message)
            is RsChangeSignatureProcessor.ParseResult.Success -> result.change
        }

    private fun applyChange(
        parameterText: String,
        returnText: String = RsChangeSignatureProcessor.returnEditorText(targetScript()),
    ) {
        val error = RsChangeSignatureProcessor.apply(project, targetScript(), parseChange(parameterText, returnText))
        assertNull(error)
        PsiDocumentManager.getInstance(project).commitAllDocuments()
    }

    private fun assertParseError(
        parameterText: String,
        expected: String,
        returnText: String = RsChangeSignatureProcessor.returnEditorText(targetScript()),
    ) {
        val result = RsChangeSignatureProcessor.parseChange(project, targetScript(), parameterText, returnText)
        assertTrue(result is RsChangeSignatureProcessor.ParseResult.Error)
        assertTrue((result as RsChangeSignatureProcessor.ParseResult.Error).message.contains(expected))
    }

    private fun assertFileContains(expected: String) {
        assertTrue("Expected file to contain:\n$expected\n\nActual:\n${myFixture.file.text}", myFixture.file.text.contains(expected))
    }

    private fun <T : java.awt.Component> findComponents(
        root: Container,
        type: Class<T>,
    ): List<T> =
        buildList {
            for (component in root.components) {
                if (type.isInstance(component)) add(type.cast(component))
                if (component is Container) addAll(findComponents(component, type))
            }
        }
}
