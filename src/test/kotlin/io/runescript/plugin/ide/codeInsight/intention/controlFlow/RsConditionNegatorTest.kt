package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsElementGenerator

class RsConditionNegatorTest : RsParserTestCase() {
    fun testNegatesComparisonOperators() {
        val cases =
            mapOf(
                "1 = 2" to "1 ! 2",
                "1 ! 2" to "1 = 2",
                "1 > 2" to "1 <= 2",
                "1 >= 2" to "1 < 2",
                "1 < 2" to "1 >= 2",
                "1 <= 2" to "1 > 2",
            )

        cases.forEach { (condition, expected) ->
            val expression = RsElementGenerator.createConditionExpression(project, condition)
            assertEquals(expected, RsConditionNegator.negate(expression))
        }
    }

    fun testNegatesDisjunctionsAndParenthesizedConditions() {
        val disjunction = RsElementGenerator.createConditionExpression(project, "1 = 2 | 3 = 4")
        val parenthesized = RsElementGenerator.createConditionExpression(project, "(1 = 2)")

        assertEquals("(1 ! 2) & (3 ! 4)", RsConditionNegator.negate(disjunction))
        assertEquals("(1 ! 2)", RsConditionNegator.negate(parenthesized))
    }
}
