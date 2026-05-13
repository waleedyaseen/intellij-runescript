package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData

class RsCompletionContributorTest : BasePlatformTestCase() {
    fun testStatementCompletionIncludesSupportedKeywordsAndDeclarations() {
        configure(
            """
            [proc,main]
            {
                <caret>
            }
            """,
        )

        assertContains("if", "while", "return", "def_int", "switch_int")
        assertDoesNotContain("for", "do", "break", "continue")
    }

    fun testExpressionCompletionContainsLocalsLiteralsAndScriptsButNotStatementOnlyKeywords() {
        addScriptFixtures()
        addConstantFixtures()
        addScopedVariableFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                def_string ${"$"}name = "";
                foo(<caret>);
            }
            """,
        )

        assertContains("${"$"}count", "${"$"}name", "%player_score", "true", "false", "null", "foo", "bar")
        assertDoesNotContain("^answer")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testConditionCompletionUsesExpressionContext() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count >= <caret>) {
                }
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testIncompleteConditionStartCompletionUsesExpressionContext() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (<caret>
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null", "calc")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testIncompleteConditionStartCompletionRanksBooleanValuesFirst() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                def_boolean ${"$"}ready = true;
                if (<caret>
            }
            """,
        )

        assertBefore("${"$"}ready", "${"$"}count")
    }

    fun testIncompleteConditionRightHandSideCompletionUsesExpressionContext() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count = <caret>
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null", "calc")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testConditionOperatorCompletionAfterLeftOperand() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count <caret>
            }
            """,
        )

        assertContains("=", "!", ">", "<", ">=", "<=")
        assertDoesNotContain("while", "return", "def_int", "${"$"}count")
    }

    fun testIncompleteCallArgumentCompletionUsesExpressionContext() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                foo(<caret>
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null", "foo", "bar")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testCallArgumentCompletionRanksExpectedTypeFirst() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                def_string ${"$"}name = "";
                ~bar(<caret>
            }
            """,
        )

        assertBefore("${"$"}name", "${"$"}count")
    }

    fun testSecondCallArgumentCompletionRanksExpectedTypeFirst() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                def_string ${"$"}name = "";
                two_args(${"$"}count, <caret>
            }
            """,
        )

        assertBefore("${"$"}name", "${"$"}count")
    }

    fun testCallArgumentCompletionSuggestsTypedConfigSymbols() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                use_obj(<caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testCallArgumentCompletionRanksExpectedConfigSymbolAtTop() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                cc_setobject(<caret>
            }
            """,
        )

        assertContains("blue_partyhat")
        assertBefore("blue_partyhat", "foo")
        assertBefore("bronze_sword", "foo")
        assertBefore("iron_sword", "foo")
        assertDoesNotContain("town_guard", "obj", "def_obj")
    }

    fun testPartialCallArgumentCompletionRanksMatchingConfigSymbolAtTop() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                cc_setobject(blue<caret>
            }
            """,
        )

        assertTop("blue_partyhat")
        assertDoesNotContain("^blue", "bronze_sword", "iron_sword", "town_guard", "foo")
    }

    fun testTypedConfigCompletionExcludesConstantsWithoutCaretMarker() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                use_obj(blue<caret>
            }
            """,
        )

        assertContains("blue_partyhat")
        assertDoesNotContain("^blue")
    }

    fun testTypedConfigCompletionIncludesConstantsWithCaretMarker() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                use_obj(^blu<caret>
            }
            """,
        )

        assertContains("^blue")
        assertDoesNotContain("blue_partyhat")
    }

    fun testPartialConfigSymbolCompletionReplacesTypedPrefix() {
        addScriptFixtures()
        addConfigSymbolFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                use_obj(bron<caret>
            }
            """,
            "bronze_sword",
        )

        assertTrue(myFixture.file.text.contains("use_obj(bronze_sword"))
        assertFalse(myFixture.file.text.contains("use_obj(bronbronze_sword"))
    }

    fun testIncompleteDeclarationInitializerCompletionUsesExpressionContext() {
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                def_int ${"$"}next = <caret>
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null", "calc")
        assertDoesNotContain("^answer")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testDeclarationInitializerCompletionRanksExpectedTypeFirst() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = "";
                def_int ${"$"}count = 1;
                def_int ${"$"}next = <caret>
            }
            """,
        )

        assertBefore("${"$"}count", "${"$"}name")
    }

    fun testDeclarationInitializerCompletionRanksScriptReturnType() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}next = <caret>
            }
            """,
        )

        assertBefore("get_count", "get_name")
    }

    fun testDeclarationInitializerCompletionRanksConstantType() {
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                def_boolean ${"$"}flag = ^<caret>
            }
            """,
        )

        assertBefore("^ready", "^answer")
    }

    fun testDeclarationInitializerCompletionRanksScopedVariableType() {
        addScopedVariableFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}score = <caret>
            }
            """,
        )

        assertContains("%player_score")
        assertDoesNotContain("%client_name")
    }

    fun testDeclarationInitializerCompletionRanksStringVarcType() {
        addScopedVariableFixtures()
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = <caret>
            }
            """,
        )

        assertContains("%client_name")
        assertDoesNotContain("%player_score")
    }

    fun testPartialScopedVariableCompletionRanksMatchingStringVarcType() {
        addScopedVariableFixtures()
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = %client<caret>
            }
            """,
        )

        assertTop("%client_name")
        assertDoesNotContain("%player_score")
    }

    fun testStringVarcCompletionIsNotStarvedByManyWrongTypeScopedVariables() {
        addManyScopedVariableFixtures()
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = <caret>
            }
            """,
        )

        assertBefore("%client_name", "null")
        assertBefore("%client_name", "calc")
        assertDoesNotContain("%int_var_1", "%int_var_200")
    }

    fun testScopedVariableCompletionInTypedCallArgumentRanksExpectedVarcTypeFirst() {
        addScriptFixtures()
        addManyNpcScopedVariableFixtures()
        configure(
            """
            [proc,test]()(npc)
            {
                mes(%<caret>);
            }
            """,
        )

        assertTop("%npc_var_8020")
        assertDoesNotContain("%int_var_1", "%int_var_200")
    }

    fun testDeclarationInitializerCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                def_obj ${"$"}item = <caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testIncompleteReturnListCompletionUsesExpressionContext() {
        configure(
            """
            [proc,main]()(int)
            {
                def_int ${"$"}count = 1;
                return(<caret>
            }
            """,
        )

        assertContains("${"$"}count", "true", "false", "null", "calc")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testReturnCompletionRanksExpectedTypeFirst() {
        configure(
            """
            [proc,main]()(int)
            {
                def_string ${"$"}name = "";
                def_int ${"$"}count = 1;
                return(<caret>
            }
            """,
        )

        assertBefore("${"$"}count", "${"$"}name")
    }

    fun testReturnCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]()(obj)
            {
                return(<caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testReturnCompletionWithCompleteCallAndNoBlockUsesScriptReturnType() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,test]()(obj)
            return(<caret>);
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard", "obj", "def_obj")
    }

    fun testReturnCompletionExcludesWrongTypeScopedVariables() {
        addConfigSymbolFixtures()
        addScopedVariableFixtures()
        configure(
            """
            [proc,test]()(obj)
            {
                return(<caret>);
            }
            """,
        )

        assertContains("blue_partyhat")
        assertDoesNotContain("%player_score")
    }

    fun testReturnCompletionRanksObjSymbolsBeforeExpressionKeywords() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,test]()(obj)
            {
                return(<caret>);
            }
            """,
        )

        assertBefore("blue_partyhat", "calc")
        assertBefore("blue_partyhat", "true")
        assertBefore("blue_partyhat", "false")
    }

    fun testReturnCompletionExcludesConstantsAtEmptyPrefix() {
        addConfigSymbolFixtures()
        addConstantFixtures()
        configure(
            """
            [proc,test]()(obj)
            {
                return(<caret>);
            }
            """,
        )

        assertContains("blue_partyhat")
        assertDoesNotContain("^blue", "^answer")
        assertBefore("blue_partyhat", "null")
    }

    fun testAssignmentCompletionRanksLeftHandSideTypeFirst() {
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = "";
                def_int ${"$"}count = 1;
                def_int ${"$"}other = 2;
                ${"$"}other = <caret>
            }
            """,
        )

        assertBefore("${"$"}count", "${"$"}name")
    }

    fun testAssignmentCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                def_obj ${"$"}item = bronze_sword;
                ${"$"}item = <caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testConditionRightHandSideCompletionRanksLeftHandSideTypeFirst() {
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = "";
                def_int ${"$"}count = 1;
                if (${"$"}count = <caret>
            }
            """,
        )

        assertBefore("${"$"}count", "${"$"}name")
    }

    fun testConditionRightHandSideCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                def_obj ${"$"}item = bronze_sword;
                if (${"$"}item = <caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testLocalVariableCompletionDoesNotTriggerAtDeclarationSite() {
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}existing = 1;
                def_int ${"$"}<caret>
            }
            """,
        )

        assertDoesNotContain("${"$"}existing")
    }

    fun testTypeNameCompletionInParametersAndReturnList() {
        configure(
            """
            [proc,main](<caret>)
            {
            }
            """,
        )

        assertContains("int", "string", "intarray")
        assertDoesNotContain("def_int", "while")

        configure(
            """
            [proc,main]()( <caret> )
            {
            }
            """,
        )

        assertContains("int", "string")
        assertDoesNotContain("def_int")
    }

    fun testScriptHeaderTriggerCompletion() {
        configure(
            """
            [<caret>,main]
            {
            }
            """,
        )

        assertContains("command", "proc", "label", "loadloc")
        assertDoesNotContain("def_int", "while")
    }

    fun testScriptReferenceContextsFilterByScriptKind() {
        addScriptFixtures()

        configure(
            """
            [proc,main]
            {
                ~<caret>
            }
            """,
        )
        assertContains("bar")
        assertDoesNotContain("foo", "client_target")

        configure(
            """
            [proc,main]
            {
                <caret>
            }
            """,
        )
        assertContains("foo", "bar")

        configure(
            """
            [proc,main]
            {
                def_int ${"$"}hook = "<caret>";
            }
            """,
        )
        assertDoesNotContain("foo", "bar")
    }

    fun testCommandCompletionRanksPrefixMatchesAtTop() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                cc_set<caret>
            }
            """,
        )

        assertTop("cc_setobject")
        assertContains("cc_setobject")
        assertDoesNotContain("def_int", "foo")
    }

    fun testCommandCompletionUsesTypedPrefixWhenPsiPositionIsWhitespace() {
        addScriptFixtures()
        configure(
            """
            [proc,test]




            cc_setobj<caret> 
            """,
        )

        assertTop("cc_setobject")
        assertContains("cc_setobject")
    }

    fun testCommandCompletionUsesSubtextPrefixMatching() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                setobject<caret>
            }
            """,
        )

        assertTop("cc_setobject")
        assertContains("cc_setobject")
        assertDoesNotContain("setobject")
    }

    fun testHookStringCompletionSuggestsClientScriptsOnly() {
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                set_hook("<caret>");
            }
            """,
        )

        assertContains("client_target")
        assertDoesNotContain("foo", "bar")
    }

    fun testSwitchKeywordCompletionUsesSwitchTypes() {
        configure(
            """
            [proc,main]
            {
                switch_<caret> (${"$"}value) {
                }
            }
            """,
        )

        assertContains("switch_int")
        assertDoesNotContain("def_int", "while")
    }

    fun testSwitchKeywordCompletionInsertsSnippetAfterPartialSwitch() {
        completeAndSelect(
            """
            [proc,main]
            {
                switch_<caret>
            }
            """,
            "switch_int",
        )

        assertTrue(myFixture.file.text.contains("switch_int () {\n}"))
        assertFalse(myFixture.file.text.contains("switch_switch_int"))
    }

    fun testSwitchBodyCompletionSuggestsCasesOnly() {
        configure(
            """
            [proc,main]
            {
                switch_int (${"$"}value) {
                    <caret>
                }
            }
            """,
        )

        assertContains("case")
        assertDoesNotContain("if", "while", "return", "def_int")
    }

    fun testSwitchCaseExpressionCompletionSuggestsDefaultAndExpressions() {
        addConstantFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}value = 1;
                switch_int (${"$"}value) {
                    case <caret>
                }
            }
            """,
        )

        assertContains("default", "${"$"}value", "true", "false", "null")
        assertDoesNotContain("^answer")
        assertDoesNotContain("while", "return", "def_int")
    }

    fun testSwitchCaseExpressionCompletionRanksSwitchTypeFirst() {
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = "";
                def_int ${"$"}value = 1;
                switch_int (${"$"}value) {
                    case <caret>
                }
            }
            """,
        )

        assertBefore("${"$"}value", "${"$"}name")
    }

    fun testSwitchCaseExpressionCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                def_obj ${"$"}item = bronze_sword;
                switch_obj (${"$"}item) {
                    case <caret>
                }
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testSwitchSelectorCompletionRanksSwitchTypeFirst() {
        configure(
            """
            [proc,main]
            {
                def_string ${"$"}name = "";
                def_int ${"$"}value = 1;
                switch_int (<caret>
            }
            """,
        )

        assertBefore("${"$"}value", "${"$"}name")
    }

    fun testSwitchSelectorCompletionSuggestsTypedConfigSymbols() {
        addConfigSymbolFixtures()
        configure(
            """
            [proc,main]
            {
                switch_obj (<caret>
            }
            """,
        )

        assertContains("bronze_sword", "iron_sword")
        assertDoesNotContain("town_guard")
    }

    fun testVariableCompletionReplacesBareDollarPrefix() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                ${"$"}<caret>
            }
            """,
            "${"$"}count",
        )

        assertTrue(myFixture.file.text.contains("${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}${"$"}count"))
    }

    fun testVariableCompletionReplacesPartialDollarPrefix() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                ${"$"}co<caret>
            }
            """,
            "${"$"}count",
        )

        assertTrue(myFixture.file.text.contains("${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}co${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}${"$"}count"))
    }

    fun testVariableCompletionWorksInsideIncompleteCondition() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}co<caret>) {
                }
            }
            """,
            "${"$"}count",
        )

        assertTrue(myFixture.file.text.contains("if (${"$"}count)"))
        assertFalse(myFixture.file.text.contains("${"$"}co${"$"}count"))
    }

    fun testVariableCompletionWorksAfterIncompleteConditionOperator() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count = ${"$"}co<caret>
            }
            """,
            "${"$"}count",
        )

        assertTrue(myFixture.file.text.contains("if (${"$"}count = ${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}co${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}${"$"}count"))
    }

    fun testVariableCompletionWorksInsideIncompleteCallArgument() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                foo(${"$"}co<caret>
            }
            """,
            "${"$"}count",
        )

        assertTrue(myFixture.file.text.contains("foo(${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}co${"$"}count"))
        assertFalse(myFixture.file.text.contains("${"$"}${"$"}count"))
    }

    fun testConstantCompletionReplacesBareCaretPrefix() {
        addConstantFixtures()
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                ^<caret>
            }
            """,
        )

        assertContains("^answer", "^ready")
        assertDoesNotContain("${"$"}count", "foo", "bar")

        selectLookup("^answer")
        myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)

        assertTrue(myFixture.file.text.contains("^answer"))
        assertFalse(myFixture.file.text.contains("^^answer"))
    }

    fun testConstantCompletionReplacesBareCaretPrefixWithoutOtherFixtureNoise() {
        addConstantFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                ^<caret>
            }
            """,
            "^answer",
        )

        assertTrue(myFixture.file.text.contains("^answer"))
        assertFalse(myFixture.file.text.contains("^^answer"))
    }

    fun testConstantCompletionReplacesPartialCaretPrefix() {
        addConstantFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                ^ans<caret>
            }
            """,
            "^answer",
        )

        assertTrue(myFixture.file.text.contains("^answer"))
        assertFalse(myFixture.file.text.contains("^ans^answer"))
        assertFalse(myFixture.file.text.contains("^^answer"))
    }

    fun testConstantCompletionWorksAfterIncompleteConditionOperator() {
        addConstantFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count = ^ans<caret>
            }
            """,
            "^answer",
        )

        assertTrue(myFixture.file.text.contains("if (${"$"}count = ^answer"))
        assertFalse(myFixture.file.text.contains("^ans^answer"))
        assertFalse(myFixture.file.text.contains("^^answer"))
    }

    fun testScopedVariableCompletionReplacesBarePercentPrefix() {
        addScopedVariableFixtures()
        addConstantFixtures()
        addScriptFixtures()
        configure(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                %<caret>
            }
            """,
        )

        assertContains("%player_score", "%client_name")
        assertDoesNotContain("${"$"}count", "^answer", "foo", "bar")

        selectLookup("%player_score")
        myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)

        assertTrue(myFixture.file.text.contains("%player_score"))
        assertFalse(myFixture.file.text.contains("%%player_score"))
    }

    fun testScopedVariableCompletionReplacesPartialPercentPrefix() {
        addScopedVariableFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                %pla<caret>
            }
            """,
            "%player_score",
        )

        assertTrue(myFixture.file.text.contains("%player_score"))
        assertFalse(myFixture.file.text.contains("%pla%player_score"))
        assertFalse(myFixture.file.text.contains("%%player_score"))
    }

    fun testScopedVariableCompletionWorksAfterIncompleteConditionOperator() {
        addScopedVariableFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count = %pla<caret>
            }
            """,
            "%player_score",
        )

        assertTrue(myFixture.file.text.contains("if (${"$"}count = %player_score"))
        assertFalse(myFixture.file.text.contains("%pla%player_score"))
        assertFalse(myFixture.file.text.contains("%%player_score"))
    }

    fun testProcCompletionDoesNotDuplicateTildePrefix() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                ~<caret>
            }
            """,
            "bar",
        )

        assertTrue(myFixture.file.text.contains("~bar()"))
        assertFalse(myFixture.file.text.contains("~~bar()"))
    }

    fun testProcCompletionDoesNotDuplicateExistingArgumentList() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                ~ba<caret>("name");
            }
            """,
            "bar",
        )

        assertTrue(myFixture.file.text.contains("~bar(\"name\");"))
        assertFalse(myFixture.file.text.contains("~bar()(\"name\");"))
        assertFalse(myFixture.file.text.contains("~babar(\"name\");"))
    }

    fun testCommandCompletionReplacesPartialName() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                fo<caret>
            }
            """,
            "foo",
        )

        assertTrue(myFixture.file.text.contains("foo()"))
        assertFalse(myFixture.file.text.contains("fofoo()"))
    }

    fun testCommandCompletionDoesNotDuplicateExistingArgumentList() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                fo<caret>(1);
            }
            """,
            "foo",
        )

        assertTrue(myFixture.file.text.contains("foo(1);"))
        assertFalse(myFixture.file.text.contains("foo()(1);"))
        assertFalse(myFixture.file.text.contains("fofoo(1);"))
    }

    fun testCommandCompletionDoesNotDuplicateExactName() {
        addScriptFixtures()
        completeAndSelect(
            """
            [proc,main]
            {
                cc_setobject<caret>
            }
            """,
            "cc_setobject",
        )

        assertTrue(myFixture.file.text.contains("cc_setobject()"))
        assertFalse(myFixture.file.text.contains("cc_setobjectcc_setobject"))
    }

    fun testIfCompletionInsertsSnippetAtStatementStart() {
        completeAndSelect(
            """
            [proc,main]
            {
                i<caret>
            }
            """,
            "if",
        )

        assertTrue(myFixture.file.text.contains("if () {\n}"))
        assertFalse(myFixture.file.text.contains("iif"))
    }

    fun testWhileCompletionInsertsSnippetAtStatementStart() {
        completeAndSelect(
            """
            [proc,main]
            {
                wh<caret>
            }
            """,
            "while",
        )

        assertTrue(myFixture.file.text.contains("while () {\n}"))
        assertFalse(myFixture.file.text.contains("whwhile"))
    }

    fun testReturnCompletionInReturningScriptInsertsReturnExpressionSnippet() {
        completeAndSelect(
            """
            [proc,main]()(int)
            {
                ret<caret>
            }
            """,
            "return",
        )

        assertTrue(myFixture.file.text.contains("return();"))
        assertFalse(myFixture.file.text.contains("retreturn"))
    }

    fun testReturnCompletionInUnitScriptInsertsBareReturn() {
        completeAndSelect(
            """
            [proc,main]
            {
                ret<caret>
            }
            """,
            "return",
        )

        assertTrue(myFixture.file.text.contains("return;"))
        assertFalse(myFixture.file.text.contains("return();"))
        assertFalse(myFixture.file.text.contains("retreturn"))
    }

    fun testSwitchTypeCompletionInsertsSwitchSnippetAtStatementStart() {
        completeAndSelect(
            """
            [proc,main]
            {
                <caret>
            }
            """,
            "switch_int",
        )

        assertTrue(myFixture.file.text.contains("switch_int () {\n}"))
    }

    fun testDefineTypeCompletionInsertsDeclarationSnippetAtStatementStart() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_i<caret>
            }
            """,
            "def_int",
        )

        assertTrue(myFixture.file.text.contains("def_int $ = ;"))
        assertFalse(myFixture.file.text.contains("def_idef_int"))
    }

    fun testCaseCompletionInsertsCaseSnippetInsideSwitchBody() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}value = 1;
                switch_int (${"$"}value) {
                    <caret>
                }
            }
            """,
            "case",
        )

        assertTrue(myFixture.file.text.contains("case :"))
    }

    fun testConditionOperatorInsertionAddsSpacing() {
        completeAndSelect(
            """
            [proc,main]
            {
                def_int ${"$"}count = 1;
                if (${"$"}count <caret>
            }
            """,
            "=",
        )

        assertTrue(myFixture.file.text.contains("if (${"$"}count = "))
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
        enableFeatures()
        myFixture.complete(CompletionType.BASIC)
    }

    private fun completeAndSelect(
        text: String,
        lookupString: String,
    ) {
        myFixture.configureByText("main.cs2", text.trimIndent())
        enableFeatures()
        myFixture.complete(CompletionType.BASIC, 2)
        if (myFixture.lookupElements.isNullOrEmpty() && myFixture.file.text.contains(lookupString)) {
            return
        }
        selectLookup(lookupString)
        myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
    }

    private fun selectLookup(lookupString: String) {
        val element =
            myFixture.lookupElements
                .orEmpty()
                .firstOrNull { lookupString in it.allLookupStrings }
                ?: error("Expected completion '$lookupString' in ${lookupStrings()}")
        myFixture.lookup.setCurrentItem(element)
    }

    private fun enableFeatures() {
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

    private fun addConstantFixtures() {
        myFixture.addFileToProject(
            "symbols/constant.sym",
            """
            answer	int	42
            ready	boolean	true
            blue	obj	blue_partyhat
            """.trimIndent() + "\n",
        )
    }

    private fun addScopedVariableFixtures() {
        myFixture.addFileToProject(
            "symbols/varp.sym",
            """
            1	player_score	int
            """.trimIndent() + "\n",
        )
        myFixture.addFileToProject(
            "symbols/varc.sym",
            """
            1	client_name	string
            """.trimIndent() + "\n",
        )
    }

    private fun addManyScopedVariableFixtures() {
        addIntVarpFixtures(200)
        myFixture.addFileToProject(
            "symbols/varc.sym",
            """
            1	client_name	string
            """.trimIndent() + "\n",
        )
    }

    private fun addManyNpcScopedVariableFixtures() {
        addIntVarpFixtures(8000)
        myFixture.addFileToProject(
            "symbols/varc.sym",
            buildString {
                var id = 0
                repeat(8000) {
                    appendScopedVariableSymbol(id, "int_var_$id", "int")
                    id++
                }
                repeat(20) {
                    appendScopedVariableSymbol(id, "string_var_$id", "string")
                    id++
                }
                repeat(2) {
                    appendScopedVariableSymbol(id, "npc_var_$id", "npc")
                    id++
                }
            },
        )
    }

    private fun addIntVarpFixtures(count: Int) {
        myFixture.addFileToProject(
            "symbols/varp.sym",
            buildString {
                for (index in 1..count) {
                    appendScopedVariableSymbol(index, "int_var_$index", "int")
                }
            },
        )
    }

    private fun StringBuilder.appendScopedVariableSymbol(
        id: Int,
        name: String,
        type: String,
    ) {
        append(id)
        append('\t')
        append(name)
        append('\t')
        append(type)
        append('\n')
    }

    private fun addScriptFixtures() {
        myFixture.addFileToProject(
            "library.cs2",
            """
            [command,foo](int ${"$"}value)
            {
            }

            [command,echo](string ${"$"}value)
            {
            }

            [command,two_args](int ${"$"}value, string ${"$"}name)
            {
            }

            [command,use_obj](obj ${"$"}obj)
            {
            }

            [command,use_npc](npc ${"$"}npc)
            {
            }

            [command,mes](npc ${"$"}npc)
            {
            }

            [command,cc_setobject](obj ${"$"}x1, int ${"$"}x2)
            {
            }

            [command,set_hook](hook ${"$"}hook)
            {
            }

            [proc,bar](string ${"$"}value)
            {
            }

            [command,get_count]()(int)
            {
            }

            [command,get_name]()(string)
            {
            }

            [clientscript,client_target]
            {
            }
            """.trimIndent(),
        )
    }

    private fun addConfigSymbolFixtures() {
        myFixture.addFileToProject(
            "symbols/obj.sym",
            """
            1	bronze_sword
            2	iron_sword
            3	blue_partyhat
            """.trimIndent() + "\n",
        )
        myFixture.addFileToProject(
            "symbols/npc.sym",
            """
            1	town_guard
            """.trimIndent() + "\n",
        )
    }

    private fun assertContains(vararg expected: String) {
        val lookupStrings = lookupStrings()
        for (value in expected) {
            assertTrue(
                "Expected completion '$value' in $lookupStrings",
                value in lookupStrings || myFixture.file.text.contains(value),
            )
        }
    }

    private fun assertDoesNotContain(vararg unexpected: String) {
        val lookupStrings = lookupStrings()
        for (value in unexpected) {
            assertFalse("Did not expect completion '$value' in $lookupStrings", value in lookupStrings)
        }
    }

    private fun assertBefore(
        expectedFirst: String,
        expectedSecond: String,
    ) {
        val lookupStrings = orderedLookupStrings()
        val firstIndex = lookupStrings.indexOf(expectedFirst)
        val secondIndex = lookupStrings.indexOf(expectedSecond)
        assertTrue("Expected '$expectedFirst' in $lookupStrings", firstIndex >= 0)
        assertTrue("Expected '$expectedSecond' in $lookupStrings", secondIndex >= 0)
        assertTrue("Expected '$expectedFirst' before '$expectedSecond' in $lookupStrings", firstIndex < secondIndex)
    }

    private fun assertTop(expected: String) {
        if (myFixture.lookupElements.isNullOrEmpty() && myFixture.file.text.contains(expected)) {
            return
        }
        val lookupStrings =
            myFixture.lookupElements
                .orEmpty()
                .map { it.lookupString }
        assertTrue("Expected '$expected' at top of $lookupStrings", lookupStrings.firstOrNull() == expected)
    }

    private fun lookupStrings(): Set<String> =
        myFixture.lookupElements
            .orEmpty()
            .flatMap { it.allLookupStrings }
            .toSet()

    private fun orderedLookupStrings(): List<String> =
        myFixture.lookupElements
            .orEmpty()
            .flatMap { it.allLookupStrings }
}
