package io.runescript.plugin.lang.parser

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsExpressionStatement
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsCompletionPsiTest : BasePlatformTestCase() {
    override fun setUp() {
        super.setUp()
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

    fun testCompletionDummyStatementRecoveryParsesAsExpressionStatement() {
        val file =
            parse(
                """
                [proc,main]
                {
                    IntellijIdeaRulezzz;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsExpressionStatement>(file))
    }

    fun testCompletionDummyConditionRecoveryParsesConditionExpression() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 0;
                    while (${"$"}i >= IntellijIdeaRulezzz) {
                    }
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsWhileStatement>(file))
        assertSize(1, children<RsConditionExpression>(file))
    }

    fun testIncompleteCompletionHolesHaveLocalizedPsiErrors() {
        parse(
            """
            [proc,main]
            {
                while (IntellijIdeaRulezzz) {
                    IntellijIdeaRulezzz
                }
            }
            """,
        )

        val errors = psiErrors()
        assertTrue("Expected at least one localized error for missing semicolon/body recovery.", errors.isNotEmpty())
        assertTrue(
            "Expected recovery to keep the while statement in the tree. Errors: ${errors.map { it.errorDescription }}",
            children<RsWhileStatement>(myFixture.file).isNotEmpty(),
        )
    }

    private fun parse(text: String) = myFixture.configureByText("completion.cs2", text.trimIndent())

    private inline fun <reified T : PsiElement> children(element: PsiElement): List<T> =
        PsiTreeUtil.findChildrenOfType(element, T::class.java).toList()

    private fun assertNoPsiErrors() {
        val errors = psiErrors()
        assertTrue(
            "Unexpected PSI errors:\n${errors.joinToString("\n") { "${it.errorDescription}: `${it.text}`" }}\n\n" +
                DebugUtil.psiToString(myFixture.file, true),
            errors.isEmpty(),
        )
    }

    private fun psiErrors(): Collection<PsiErrorElement> = PsiTreeUtil.findChildrenOfType(myFixture.file, PsiErrorElement::class.java)
}
