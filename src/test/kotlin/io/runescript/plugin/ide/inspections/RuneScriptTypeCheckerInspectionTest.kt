package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.inspections.fixes.RsCreateScriptQuickFix
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsStringLiteralContent

class RuneScriptTypeCheckerInspectionTest : RsParserTestCase() {
    fun testIncompleteLocalDeclarationDoesNotCrashHighlighting() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                def_int ${"$"} = 0;
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        myFixture.doHighlighting()
    }

    fun testUnresolvedCallsOfferScriptCreationFixes() {
        myFixture.addFileToProject("neptune.toml", "")
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    missing_command();
                    ~missing_proc();
                }
                """.trimIndent(),
            )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fixes = myFixture.getAllQuickFixes().associateBy { it.text }

        assertContainsElements(
            fixes.keys,
            "Create script ('missing_command')",
            "Create script ('missing_proc')",
        )
        myFixture.launchAction(fixes.getValue("Create script ('missing_proc')"))
        assertTrue(file.text.contains("[proc,missing_proc]"))
    }

    fun testMissingCallArgumentsCanBeInsertedWithTypeDefaults() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,configure](int ${"$"}id,string ${"$"}name,boolean ${"$"}enabled)
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                configure(42);
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Insert 2 missing arguments" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main]
            {
                configure(42, "", false);
            }
            """.trimIndent(),
        )
    }

    fun testSameArityTypeMismatchDoesNotOfferMissingArgumentsFix() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,takes_int](int ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                takes_int("wrong");
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        assertFalse(myFixture.getAllQuickFixes().any { action -> action.text.contains("missing argument") })
    }

    fun testExtraCallArgumentsCanBeRemoved() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,takes_one](int ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                takes_one(1, 2, 3);
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Remove 2 extra arguments" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main]
            {
                takes_one(1);
            }
            """.trimIndent(),
        )
    }

    fun testArgumentsCanBeRemovedFromNoArgumentCall() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,takes_none]
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                takes_none(1);
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Remove extra argument" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main]
            {
                takes_none();
            }
            """.trimIndent(),
        )
    }

    fun testUnresolvedLocalCanBeCreatedFromExpectedType() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,takes_string](string ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                takes_string(${"$"}missing);
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Create local variable '${"$"}missing'" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main]
            {
                def_string ${"$"}missing = "";
                takes_string(${"$"}missing);
            }
            """.trimIndent(),
        )
    }

    fun testUnresolvedLocalWithoutExpectedTypeHasNoCreationFix() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                ${"$"}missing;
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        assertFalse(myFixture.getAllQuickFixes().any { action -> action.text.contains("Create local variable") })
    }

    fun testUnresolvedConstantCanBeCreatedFromExpectedType() {
        myFixture.addFileToProject("neptune.toml", "")
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
        val constantFile = myFixture.addFileToProject("symbols/constant.sym", "existing\tint\t1\n")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,takes_int](int ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                takes_int(^missing);
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Create constant '^missing'" }
        myFixture.launchAction(fix)

        val psiFile = PsiManager.getInstance(project).findFile(constantFile.virtualFile)!!
        assertEquals("existing\tint\t1\nmissing\tint\t0\n", psiFile.text)
    }

    fun testLocalDeclarationCanAdoptInitializerType() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                def_int ${"$"}name = "RuneScript";
            }
            """.trimIndent(),
        )
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())

        val fix = myFixture.getAllQuickFixes().single { action -> action.text == "Change local type to 'string'" }
        myFixture.launchAction(fix)

        myFixture.checkResult(
            """
            [proc,main]
            {
                def_string ${"$"}name = "RuneScript";
            }
            """.trimIndent(),
        )
    }

    fun testUnresolvedInjectedHookCanCreateClientscript() {
        myFixture.addFileToProject("neptune.toml", "")
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,set_hook](hook ${"$"}hook)
            {
            }
            """.trimIndent(),
        )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    set_hook("missing_clientscript");
                }
                """.trimIndent(),
            )
        val host = PsiTreeUtil.findChildOfType(file, RsStringLiteralContent::class.java)!!
        val injectedFile =
            InjectedLanguageManager
                .getInstance(project)
                .getInjectedPsiFiles(host)!!
                .single()
                .first
        val hook = PsiTreeUtil.findChildOfType(injectedFile, RsHookFragment::class.java)!!
        val fix = RsCreateScriptQuickFix("clientscript", "missing_clientscript")
        val descriptor =
            InspectionManager
                .getInstance(project)
                .createProblemDescriptor(
                    hook,
                    "Unresolved clientscript",
                    fix,
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL,
                    true,
                )

        WriteCommandAction.runWriteCommandAction(project) {
            fix.applyFix(project, descriptor)
        }

        assertTrue(file.text.contains("[clientscript,missing_clientscript]"))
    }
}
