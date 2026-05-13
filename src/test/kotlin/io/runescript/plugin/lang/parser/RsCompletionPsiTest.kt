package io.runescript.plugin.lang.parser

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsExpressionStatement
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsCompletionPsiTest : RsParserTestCase() {
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

        assertTrue(
            "Expected recovery to keep the while statement in the tree. Errors: ${psiErrors().map { it.errorDescription }}",
            children<RsWhileStatement>(myFixture.file).isNotEmpty(),
        )
    }

    fun testCompletionDummyConditionRhsRecoveryKeepsFollowingBlock() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 0;
                    while (${"$"}i >= IntellijIdeaRulezzz) {
                        ${"$"}i = 1;
                    }
                }
                """,
            )

        assertSize(1, children<RsWhileStatement>(file))
        assertSize(1, children<RsConditionExpression>(file))
        assertTrue(children<RsLocalVariableExpression>(file).any { it.name == "i" && it.text.startsWith("${"$"}") })
        assertNoPsiErrors()
    }

    fun testIncompleteCallRecoveryKeepsCommandExpression() {
        val file =
            parse(
                """
                [proc,main]
                {
                    foo(IntellijIdeaRulezzz
                }
                """,
            )

        assertSize(1, children<RsExpressionStatement>(file))
        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("foo") })
        assertErrorCountAtMost(1)
    }

    fun testIncompleteCallRecoveryKeepsFollowingStatement() {
        val file =
            parse(
                """
                [proc,main]
                {
                    foo(IntellijIdeaRulezzz
                    bar();
                }
                """,
            )

        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("foo") })
        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("bar") })
        assertTrue(children<RsExpressionStatement>(file).size >= 2)
        assertErrorCountAtMost(1)
    }

    fun testCompletionDummyDeclarationRecoveryKeepsDeclarationAndNextStatement() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}IntellijIdeaRulezzz
                    foo();
                }
                """,
            )

        assertSize(1, children<RsLocalVariableDeclarationStatement>(file))
        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("foo") })
        assertErrorCountAtMost(1)
    }

    fun testMissingReturnSemicolonRecoveryKeepsFollowingStatement() {
        val file =
            parse(
                """
                [proc,main]
                {
                    return
                    foo();
                }
                """,
            )

        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("foo") })
        assertErrorCountAtMost(1)
    }

    fun testMissingAssignmentSemicolonRecoveryKeepsFollowingStatement() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                    ${"$"}value = 2
                    foo();
                }
                """,
            )

        assertTrue(children<RsCommandExpression>(file).any { it.text.startsWith("foo") })
        assertErrorCountAtMost(1)
    }

    fun testIncompleteProcAndCommandStartsStayInExpressionStatements() {
        val file =
            parse(
                """
                [proc,main]
                {
                    ~hel;
                    fo;
                }
                """,
            )

        assertTrue(children<RsGosubExpression>(file).any { it.text.startsWith("~hel") })
        assertTrue(children<RsDynamicExpression>(file).any { it.text.startsWith("fo") })
        assertSize(2, children<RsExpressionStatement>(file))
        assertNoPsiErrors()
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

    private fun assertErrorCountAtMost(max: Int) {
        val errors = psiErrors()
        assertTrue(
            "Expected at most $max PSI errors, got ${errors.size}:\n" +
                "${errors.joinToString("\n") { "${it.errorDescription}: `${it.text}`" }}\n\n" +
                DebugUtil.psiToString(myFixture.file, true),
            errors.size <= max,
        )
    }

    private fun psiErrors(): Collection<PsiErrorElement> = PsiTreeUtil.findChildrenOfType(myFixture.file, PsiErrorElement::class.java)
}
