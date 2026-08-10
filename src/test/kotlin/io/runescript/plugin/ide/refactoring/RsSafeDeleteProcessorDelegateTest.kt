package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegate
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteCustomUsageInfo
import com.intellij.refactoring.safeDelete.usageInfo.SafeDeleteReferenceUsageInfo
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.usageView.UsageInfo
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scriptName

class RsSafeDeleteProcessorDelegateTest : BasePlatformTestCase() {
    private val delegate = RsSafeDeleteProcessorDelegate()

    fun testParameterDeletionUpdatesCallsAndDocumentation() {
        configure(
            """
            /**
             * @param first
             * @param middle remove me
             *   continuation
             * @param last
             */
            [proc,target](int ${"$"}first, string ${"$"}middle, int ${"$"}last)
            {
                foo(${"$"}first, ${"$"}last);
            }

            [proc,caller]
            {
                ~target(1, "two", 3);
                ~target(4, "five", 6);
            }
            """,
        )

        safeDelete(parameter("middle").localVariableExpression!!)

        assertFileContains("[proc,target](int ${"$"}first, int ${"$"}last)")
        assertFileContains("~target(1, 3);")
        assertFileContains("~target(4, 6);")
        assertFalse(myFixture.file.text.contains("@param middle"))
        assertFalse(myFixture.file.text.contains("continuation"))
    }

    fun testDeletesFirstAndLastItemsWithoutDanglingCommas() {
        configure(
            """
            [proc,target](int ${"$"}first, int ${"$"}middle, int ${"$"}last)
            {
            }

            [proc,caller]
            {
                ~target(1, 2, 3);
            }
            """,
        )

        safeDelete(parameter("first").localVariableExpression!!)

        assertFileContains("[proc,target](int ${"$"}middle, int ${"$"}last)")
        assertFileContains("~target(2, 3);")

        safeDelete(parameter("last").localVariableExpression!!)

        assertFileContains("[proc,target](int ${"$"}middle)")
        assertFileContains("~target(2);")
    }

    fun testBodyReferenceIsUnsafeAndIsNotAutomaticallyDeleted() {
        configure(
            """
            [proc,target](int ${"$"}value)
            {
                foo(${"$"}value);
            }
            """,
        )
        val variable = parameter("value").localVariableExpression!!

        val usages = findUsages(variable)

        val referenceUsages = usages.filterIsInstance<SafeDeleteReferenceUsageInfo>()
        assertEquals(1, referenceUsages.size)
        assertFalse(referenceUsages.single().isSafeDelete)
        assertFileContains("foo(${"$"}value);")
    }

    fun testMalformedCallIsUnsafeInsteadOfDeletingWrongArgument() {
        configure(
            """
            [proc,target](int ${"$"}first, int ${"$"}second)
            {
            }

            [proc,caller]
            {
                ~target(1);
            }
            """,
        )

        val usages = findUsages(parameter("second").localVariableExpression!!)

        val referenceUsages = usages.filterIsInstance<SafeDeleteReferenceUsageInfo>()
        assertEquals(1, referenceUsages.size)
        assertFalse(referenceUsages.single().isSafeDelete)
        assertFileContains("~target(1);")
    }

    fun testLocalDeletionPreservesCallInitializer() {
        configure(
            """
            [proc,target]
            {
                def_int ${"$"}unused = ~load_value();
            }
            """,
        )
        val variable = localDeclaration("unused")

        safeDelete(variable)

        assertFileContains("~load_value();")
        assertFalse(myFixture.file.text.contains("def_int"))
    }

    fun testLocalDeletionDropsSafeInitializer() {
        configure(
            """
            [proc,target]
            {
                def_int ${"$"}unused = 42;
            }
            """,
        )

        safeDelete(localDeclaration("unused"))

        assertFalse(myFixture.file.text.contains("unused"))
        assertFalse(myFixture.file.text.contains("42;"))
    }

    fun testReferencedScriptIsReportedUnsafeAndCallIsKept() {
        configure(
            """
            /** target docs */
            [proc,target]
            {
            }

            [proc,caller]
            {
                ~target;
            }
            """,
        )
        val target = targetScript()

        val usages = findUsages(target)

        val referenceUsages = usages.filterIsInstance<SafeDeleteReferenceUsageInfo>()
        assertEquals(1, referenceUsages.size)
        assertFalse(referenceUsages.single().isSafeDelete)

        WriteCommandAction.runWriteCommandAction(project) {
            delegate.prepareForDeletion(target)
            if (target.isValid) target.delete()
        }
        commitDocuments()

        assertFalse(myFixture.file.text.contains("target docs"))
        assertFalse(myFixture.file.text.contains("[proc,target]"))
        assertFileContains("~target;")
    }

    fun testAvailabilityIsLimitedToSupportedDeclarations() {
        configure(
            """
            [proc,target](int ${"$"}parameter)
            {
                def_int ${"$"}local = ${"$"}parameter;
                foo(${"$"}local);
            }
            """,
        )
        val provider = RsRefactoringSupportProvider()

        assertTrue(provider.isSafeDeleteAvailable(targetScript()))
        assertTrue(provider.isSafeDeleteAvailable(parameter("parameter")))
        assertTrue(provider.isSafeDeleteAvailable(parameter("parameter").localVariableExpression!!))
        assertTrue(provider.isSafeDeleteAvailable(localDeclaration("local")))
        val usage =
            PsiTreeUtil
                .findChildrenOfType(myFixture.file, RsLocalVariableExpression::class.java)
                .first { variable -> variable.text == "${"$"}local" && variable !== localDeclaration("local") }
        assertFalse(provider.isSafeDeleteAvailable(usage))
    }

    fun testRegistersExactlyOneRuneScriptSafeDeleteProcessor() {
        val processors =
            SafeDeleteProcessorDelegate.EP_NAME.extensionList.filterIsInstance<RsSafeDeleteProcessorDelegate>()

        assertEquals(1, processors.size)
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

    private fun parameter(name: String): RsParameter =
        targetScript().parameterList!!.parameterList.first { parameter -> parameter.localVariableExpression?.name == name }

    private fun localDeclaration(name: String): RsLocalVariableExpression =
        PsiTreeUtil
            .findChildrenOfType(targetScript(), RsLocalVariableDeclarationStatement::class.java)
            .mapNotNull { declaration -> declaration.expressionList.firstOrNull() as? RsLocalVariableExpression }
            .first { variable -> variable.name == name }

    private fun findUsages(element: com.intellij.psi.PsiElement): MutableList<UsageInfo> =
        mutableListOf<UsageInfo>().also { usages -> delegate.findUsages(element, arrayOf(element), usages) }

    private fun safeDelete(element: com.intellij.psi.PsiElement) {
        val usages = findUsages(element)
        WriteCommandAction.runWriteCommandAction(project) {
            usages.filterIsInstance<SafeDeleteCustomUsageInfo>().forEach(SafeDeleteCustomUsageInfo::performRefactoring)
            delegate.prepareForDeletion(element)
            if (element.isValid) element.delete()
        }
        commitDocuments()
    }

    private fun commitDocuments() {
        val manager = PsiDocumentManager.getInstance(project)
        manager.getDocument(myFixture.file)?.let(manager::doPostponedOperationsAndUnblockDocument)
        manager.commitAllDocuments()
    }

    private fun assertFileContains(expected: String) {
        assertTrue("Expected file to contain:\n$expected\n\nActual:\n${myFixture.file.text}", myFixture.file.text.contains(expected))
    }
}
