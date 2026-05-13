package io.runescript.plugin.lang.parser

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.util.PsiTreeUtil

class RsErroneousGrammarPsiTest : RsParserTestCase() {
    fun testIfWithoutBodyReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                def_int ${"$"}test = 0;
                if (${"$"}test = 1)
            }
            """,
        )
    }

    fun testIfWithoutClosingConditionParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                if (true {
                }
            }
            """,
        )
    }

    fun testWhileWithoutBodyReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                while (true)
            }
            """,
        )
    }

    fun testWhileWithoutClosingConditionParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                while (true {
                }
            }
            """,
        )
    }

    fun testIfElseWithoutFalseBodyReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                if (true) {
                } else
            }
            """,
        )
    }

    fun testSwitchWithoutBodyReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                switch_int (${"$"}test)
            }
            """,
        )
    }

    fun testSwitchWithoutClosingConditionParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                switch_int (${"$"}test {
                }
            }
            """,
        )
    }

    fun testSwitchWithoutClosingBraceReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                switch_int (${"$"}test) {
            }
            """,
        )
    }

    fun testSwitchCaseWithoutColonReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                switch_int (${"$"}test) {
                    case 1
                        return;
                }
            }
            """,
        )
    }

    fun testReturnWithoutSemicolonReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                return
            }
            """,
        )
    }

    fun testReturnWithoutClosingParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                return(1;
            }
            """,
        )
    }

    fun testDeclarationWithoutVariableReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                def_int = 1;
            }
            """,
        )
    }

    fun testDeclarationWithoutSemicolonReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                def_int ${"$"}test = 1
            }
            """,
        )
    }

    fun testArrayDeclarationWithoutSizeReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                def_intarray ${"$"}values();
            }
            """,
        )
    }

    fun testArrayDeclarationWithoutClosingParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                def_intarray ${"$"}values(1;
            }
            """,
        )
    }

    fun testAssignmentWithoutRightHandSideReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                ${"$"}test = ;
            }
            """,
        )
    }

    fun testAssignmentWithoutSemicolonReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                ${"$"}test = 1
            }
            """,
        )
    }

    fun testExpressionStatementWithoutSemicolonReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                foo()
            }
            """,
        )
    }

    fun testArgumentListWithoutClosingParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                foo(1;
            }
            """,
        )
    }

    fun testArrayAccessWithoutClosingParenReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                ${"$"}values(1 = 2;
            }
            """,
        )
    }

    fun testConditionWithoutRightHandSideReportsError() {
        assertHasPsiErrors(
            """
            [proc,test]
            {
                if (${"$"}test = ) {
                }
            }
            """,
        )
    }

    fun testMissingScriptBodyStillReportsErrorsInsideFollowingTopLevelText() {
        assertHasPsiErrors(
            """
            [proc,test]
            def_int ${"$"}test = 0;
            if (${"$"}test = 1)
            """,
        )
    }

    private fun assertHasPsiErrors(text: String) {
        myFixture.configureByText("erroneous.cs2", text.trimIndent())
        val errors = PsiTreeUtil.findChildrenOfType(myFixture.file, PsiErrorElement::class.java)
        assertTrue(
            "Expected PSI errors but parse was clean:\n${DebugUtil.psiToString(myFixture.file, true)}",
            errors.isNotEmpty(),
        )
    }
}
