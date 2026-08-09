package io.runescript.plugin.ide.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsStringLiteralContent

class RuneScriptTypeCheckerInspectionTest : RsParserTestCase() {
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
