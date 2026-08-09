package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsAssignmentStatement
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsPostfixExpression
import io.runescript.plugin.lang.psi.RsPrefixExpression

class RsReadWriteAccessDetectorTest : RsParserTestCase() {
    private val detector = RsReadWriteAccessDetector()

    fun testDeclarationAccessReflectsInitialization() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main](int ${"$"}parameter)
                {
                    def_int ${"$"}uninitialized;
                    def_int ${"$"}initialized = 1;
                    def_intarray ${"$"}values(10);
                }
                """.trimIndent(),
            )
        val parameter = PsiTreeUtil.findChildOfType(file, RsParameter::class.java)!!.localVariableExpression!!
        val declarations = PsiTreeUtil.findChildrenOfType(file, RsLocalVariableDeclarationStatement::class.java).toList()
        val arrayDeclaration = PsiTreeUtil.findChildOfType(file, RsArrayVariableDeclarationStatement::class.java)!!

        assertTrue(detector.isDeclarationWriteAccess(parameter))
        assertFalse(detector.isDeclarationWriteAccess(declarations[0].variable))
        assertTrue(detector.isDeclarationWriteAccess(declarations[1].variable))
        assertTrue(detector.isDeclarationWriteAccess(arrayDeclaration.variable))
    }

    fun testAssignmentDistinguishesTargetsFromValuesAndIndexes() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                    def_int ${"$"}other = 2;
                    def_intarray ${"$"}values(10);
                    ${"$"}value = ${"$"}other;
                    ${"$"}values(${"$"}other) = ${"$"}value;
                }
                """.trimIndent(),
            )
        val assignments = PsiTreeUtil.findChildrenOfType(file, RsAssignmentStatement::class.java).toList()
        val direct = assignments[0].expressionList
        val arrayAssignment = assignments[1]
        val arrayAccess = arrayAssignment.expressionList[0] as RsArrayAccessExpression

        assertEquals(Access.Write, detector.getExpressionAccess(direct[0]))
        assertEquals(Access.Read, detector.getExpressionAccess(direct[1]))
        assertEquals(Access.Write, detector.getExpressionAccess(arrayAccess.expressionList[0]))
        assertEquals(Access.Read, detector.getExpressionAccess(arrayAccess.expressionList[1]))
        assertEquals(Access.Read, detector.getExpressionAccess(arrayAssignment.expressionList[1]))
    }

    fun testIncrementAndDecrementAreReadWriteAccesses() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                    ++${"$"}value;
                    ${"$"}value--;
                }
                """.trimIndent(),
            )
        val prefix = PsiTreeUtil.findChildOfType(file, RsPrefixExpression::class.java)!!
        val postfix = PsiTreeUtil.findChildOfType(file, RsPostfixExpression::class.java)!!

        assertEquals(Access.ReadWrite, detector.getExpressionAccess(prefix.expression))
        assertEquals(Access.ReadWrite, detector.getExpressionAccess(postfix.expression))
    }
}
