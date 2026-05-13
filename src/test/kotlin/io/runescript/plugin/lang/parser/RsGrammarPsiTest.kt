package io.runescript.plugin.lang.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.impl.DebugUtil
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsArithmeticOp
import io.runescript.plugin.lang.psi.RsArrayAccessExpression
import io.runescript.plugin.lang.psi.RsArrayVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsAssignmentStatement
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsBooleanLiteralExpression
import io.runescript.plugin.lang.psi.RsCalcExpression
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsConditionExpression
import io.runescript.plugin.lang.psi.RsConditionOp
import io.runescript.plugin.lang.psi.RsConstantExpression
import io.runescript.plugin.lang.psi.RsCoordLiteralExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsEmptyStatement
import io.runescript.plugin.lang.psi.RsExpressionStatement
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsIfStatement
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.RsLocalVariableDeclarationStatement
import io.runescript.plugin.lang.psi.RsLongLiteralExpression
import io.runescript.plugin.lang.psi.RsNullLiteralExpression
import io.runescript.plugin.lang.psi.RsParExpression
import io.runescript.plugin.lang.psi.RsPostfixExpression
import io.runescript.plugin.lang.psi.RsPrefixExpression
import io.runescript.plugin.lang.psi.RsReturnStatement
import io.runescript.plugin.lang.psi.RsScopedVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStringInterpolationExpression
import io.runescript.plugin.lang.psi.RsStringLiteralContent
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.RsWhileStatement

class RsGrammarPsiTest : BasePlatformTestCase() {
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

    fun testScriptHeaderParametersReturnsAndBlockParse() {
        val file =
            parse(
                """
                [proc,main](int ${"$"}value, string ${"$"}name)(int, string)
                {
                    return(${"$"}value, ${"$"}name);
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsScript>(file))
        assertSize(1, children<RsReturnStatement>(file))
        assertSize(1, children<RsBlockStatement>(file))
    }

    fun testScriptHeaderOptionalPartsParse() {
        val file =
            parse(
                """
                [proc,noargs]()
                {
                    return;
                }

                [proc,emptyreturns]()()
                {
                    return();
                }

                [proc,starred*]
                {
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(3, children<RsScript>(file))
        assertSize(2, children<RsReturnStatement>(file))
        assertEquals("*", children<RsScript>(file)[2].star?.text)
    }

    fun testIfElseWhileAndBooleanConditionsParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 0;
                    if ((${"$"}i >= 0 & true) | false) {
                        ${"$"}i = calc(${"$"}i + 1);
                    } else {
                        ${"$"}i = 1;
                    }
                    if (${"$"}i ! 0) {
                    }
                    if (${"$"}i <= 10) {
                    }
                    if (${"$"}i = 1) {
                    }
                    if (${"$"}i > 1) {
                    }
                    while (${"$"}i < 10) {
                        ${"$"}i = calc(${"$"}i + 1);
                    }
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(5, children<RsIfStatement>(file))
        assertSize(1, children<RsWhileStatement>(file))
        assertTrue(children<RsConditionExpression>(file).isNotEmpty())
        assertTrue(children<RsConditionOp>(file).map { it.text }.containsAll(listOf("|", "&", ">=", "!", "<=", "=", ">", "<")))
    }

    fun testControlFlowCanUseSingleStatementBodies() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 0;
                    if (true)
                        ${"$"}i = 1;
                    else
                        ${"$"}i = 2;
                    while (${"$"}i < 10)
                        ${"$"}i = calc(${"$"}i + 1);
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsIfStatement>(file))
        assertSize(1, children<RsWhileStatement>(file))
        assertSize(3, children<RsAssignmentStatement>(file))
    }

    fun testCommentsCanAppearBetweenGrammarElements() {
        val file =
            parse(
                """
                [proc,main]
                {
                    // before declaration
                    def_int ${"$"}i = 1; /* after declaration */
                    /** doc comment */
                    if (true) {
                    }
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsLocalVariableDeclarationStatement>(file))
        assertSize(1, children<RsIfStatement>(file))
    }

    fun testDeclarationsAssignmentsExpressionAndEmptyStatementsParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 1;
                    def_string ${"$"}name;
                    ${"$"}i = 2;
                    ${"$"}i, ${"$"}name = 3, "three";
                    foo(${"$"}i);
                    ;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(2, children<RsLocalVariableDeclarationStatement>(file))
        assertSize(2, children<RsAssignmentStatement>(file))
        assertSize(1, children<RsExpressionStatement>(file))
        assertSize(1, children<RsEmptyStatement>(file))
    }

    fun testArrayDeclarationAccessAndUnaryExpressionsParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_intarray ${"$"}values(10);
                    def_int ${"$"}i = 0;
                    %varp = 1;
                    ${"$"}values(${"$"}i) = 42;
                    ++${"$"}i;
                    ${"$"}i--;
                    ++${"$"}values(${"$"}i);
                    --%varp;
                    %varp++;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsArrayVariableDeclarationStatement>(file))
        assertSize(2, children<RsArrayAccessExpression>(file))
        assertSize(3, children<RsPrefixExpression>(file))
        assertSize(2, children<RsPostfixExpression>(file))
    }

    fun testSwitchCasesParseDefaultAndExpressions() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}i = 1;
                    switch_int (${"$"}i) {
                    }
                    switch_int (${"$"}i) {
                        case 1, 2 :
                            ${"$"}i = 3;
                        case default :
                            ${"$"}i = 4;
                    }
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(2, children<RsSwitchStatement>(file))
        assertSize(2, children<RsSwitchCase>(file))
    }

    fun testSwitchCaseColonWithoutSpaceIsCurrentlyPartOfIdentifier() {
        parse(
            """
            [proc,main]
            {
                def_int ${"$"}i = 1;
                switch_int (${"$"}i) {
                    case 1:
                        ${"$"}i = 3;
                }
            }
            """,
        )

        val errors = psiErrors()
        assertTrue("Expected switch case without a spaced colon to expose the lexer boundary.", errors.isNotEmpty())
        assertTrue(errors.any { it.errorDescription.contains("expected") })
    }

    fun testCommandGosubStarCommandConstantAndDynamicExpressionsParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    empty();
                    foo(1, "x");
                    ~helper;
                    ~helper(${"$"}arg);
                    bar * (1) (2);
                    baz * () ();
                    ^constant_name;
                    dynamic_name;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(4, children<RsCommandExpression>(file))
        assertSize(2, children<RsGosubExpression>(file))
        assertTrue(children<RsCommandExpression>(file).any { it.isStar })
        assertSize(1, children<RsConstantExpression>(file))
        assertTrue(children<RsDynamicExpression>(file).isNotEmpty())
    }

    fun testKeywordsCanBeNameLiteralsWhereGrammarAllowsNames() {
        val file =
            parse(
                """
                [while,if](int ${"$"}true)
                {
                    def_int ${"$"}false = 1;
                    ^while;
                    ^null;
                }

                [int,def_int]
                {
                    ^switch_int;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(2, children<RsScript>(file))
        assertSize(1, children<RsLocalVariableDeclarationStatement>(file))
        assertSize(3, children<RsConstantExpression>(file))
    }

    fun testLiteralAndParenthesizedExpressionsParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}integer = -123;
                    def_int ${"$"}hex = 0x7b;
                    def_int ${"$"}long = -0x7bL;
                    def_coord ${"$"}coord = 0_50_50_10_10;
                    def_int ${"$"}grouped = (1);
                    def_int ${"$"}truth = true;
                    def_int ${"$"}lie = false;
                    def_int ${"$"}nothing = null;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(3, children<RsIntegerLiteralExpression>(file))
        assertSize(1, children<RsLongLiteralExpression>(file))
        assertSize(1, children<RsCoordLiteralExpression>(file))
        assertSize(1, children<RsParExpression>(file))
        assertSize(2, children<RsBooleanLiteralExpression>(file))
        assertSize(1, children<RsNullLiteralExpression>(file))
    }

    fun testCalcParsesEveryArithmeticOperator() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_int ${"$"}value = calc(((1 + 2 - 3) * 4 / 5 % 6) & 7 | 8);
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsCalcExpression>(file))
        assertTrue(children<RsArithmeticOp>(file).map { it.text }.containsAll(listOf("+", "-", "*", "/", "%", "&", "|")))
    }

    fun testStringTagsEscapesAndInterpolationParse() {
        val file =
            parse(
                """
                [proc,main]
                {
                    def_string ${"$"}message = "value: <calc(${"$"}value + 1)> <col=ff00ff>pink</col> <br> \"quote\" <shad=";
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsStringLiteralExpression>(file))
        assertSize(1, children<RsStringLiteralContent>(file))
        assertSize(1, children<RsStringInterpolationExpression>(file))
    }

    fun testScopedVariableExpressionParses() {
        val file =
            parse(
                """
                [proc,main]
                {
                    %varp = 1;
                }
                """,
            )

        assertNoPsiErrors()
        assertSize(1, children<RsScopedVariableExpression>(file))
        assertSize(1, children<RsAssignmentStatement>(file))
    }

    fun testHookRootFragmentAndTransmitListParse() {
        val hookRoot = parseHook("target(1, ${"$"}value) {${"$"}value, true}")
        val bareHookRoot = parseHook("target")
        val emptyHookRoot = parseHook("target() {}")

        assertTrue(hasNodeType(hookRoot, RsElementTypes.HOOK_FRAGMENT))
        assertTrue(hasNodeType(hookRoot, RsElementTypes.HOOK_TRANSMIT_LIST))
        assertFalse(DebugUtil.nodeTreeToString(hookRoot, true).contains("PsiError"))
        assertTrue(hasNodeType(bareHookRoot, RsElementTypes.HOOK_FRAGMENT))
        assertFalse(DebugUtil.nodeTreeToString(bareHookRoot, true).contains("PsiError"))
        assertTrue(hasNodeType(emptyHookRoot, RsElementTypes.HOOK_FRAGMENT))
        assertTrue(hasNodeType(emptyHookRoot, RsElementTypes.HOOK_TRANSMIT_LIST))
        assertFalse(DebugUtil.nodeTreeToString(emptyHookRoot, true).contains("PsiError"))
    }

    private fun parse(text: String) = myFixture.configureByText("grammar.cs2", text.trimIndent())

    private fun parseHook(text: String): ASTNode {
        val lexer = RsLexerAdapter(RsLexerInfo(module.neptuneModuleData.resolvedData.types))
        val builder = PsiBuilderFactory.getInstance().createBuilder(RsParserDefinition(), lexer, text)
        return RsParser().parse(RsElementTypes.HOOK_ROOT, builder)
    }

    private fun hasNodeType(
        node: ASTNode,
        type: IElementType,
    ): Boolean {
        if (node.elementType == type) return true
        var child = node.firstChildNode
        while (child != null) {
            if (hasNodeType(child, type)) return true
            child = child.treeNext
        }
        return false
    }

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
