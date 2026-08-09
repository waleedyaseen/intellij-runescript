package io.runescript.plugin.ide.injection

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.RuneScriptHook
import io.runescript.plugin.lang.parser.RsHookFileElementType
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStringLiteralContent
import io.runescript.plugin.lang.psi.typechecker.TypeCheckingUtil
import io.runescript.plugin.lang.psi.typechecker.typeCheckerData

class RsHookLanguageInjectorTest : RsParserTestCase() {
    fun testInjectsHookFragmentIntoHookArgument() {
        addHookCommand()
        val file = configure("set_hook(\"target(1) {true}\");")

        val injectedFile = injectedFile(file)
        val hook = PsiTreeUtil.findChildOfType(injectedFile, RsHookFragment::class.java)

        assertSame(RuneScriptHook, injectedFile.language)
        assertSame(RsHookFileElementType, injectedFile.node.elementType)
        assertNotNull(hook)
        assertEquals("target", hook!!.nameLiteral.text)
        assertEquals("(1)", hook.argumentList?.text)
        assertEquals("{true}", hook.hookTransmitList?.text)
    }

    fun testDoesNotInjectOrdinaryStringArgument() {
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,echo](string ${"$"}text)
            {
            }
            """.trimIndent(),
        )
        val file = configure("echo(\"target\");")

        assertNull(injectedFileOrNull(file))
    }

    fun testDoesNotInjectInterpolatedHookString() {
        addHookCommand()
        val file = configure("set_hook(\"target(<${"$"}value>)\");")

        assertNull(injectedFileOrNull(file))
    }

    fun testHookTypeCheckingInitializesHostScope() {
        addHookCommand()
        myFixture.addFileToProject(
            "target.cs2",
            """
            [clientscript,target](int ${"$"}value)
            {
            }
            """.trimIndent(),
        )
        val file =
            configure(
                """
                def_int ${"$"}value = 1;
                set_hook("target(${"$"}value)");
                """.trimIndent(),
            )
        val hook = PsiTreeUtil.findChildOfType(injectedFile(file), RsHookFragment::class.java)

        assertNotNull(hook)
        assertEmpty(TypeCheckingUtil.typeCheck(hook!!))
    }

    fun testHookCheckingReusesHostAndInjectedAnalysis() {
        addHookCommand()
        myFixture.addFileToProject(
            "target.cs2",
            """
            [clientscript,target]
            {
            }
            """.trimIndent(),
        )
        val file = configure("set_hook(\"target\");")
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!
        val hook = PsiTreeUtil.findChildOfType(injectedFile(file), RsHookFragment::class.java)!!

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        val hostData = script.typeCheckerData
        assertEmpty(TypeCheckingUtil.typeCheck(hook))
        val hookData = hook.typeCheckerData

        assertEmpty(TypeCheckingUtil.typeCheck(hook))
        assertSame(hostData, script.typeCheckerData)
        assertSame(hookData, hook.typeCheckerData)
    }

    fun testInjectionIsInvalidatedWhenCommandSignatureChanges() {
        val commands =
            myFixture.addFileToProject(
                "commands.cs2",
                """
                [command,set_value](string ${"$"}value)
                {
                }
                """.trimIndent(),
            )
        val file = configure("set_value(\"target\");")

        assertNull(injectedFileOrNull(file))

        WriteCommandAction.runWriteCommandAction(project) {
            commands.viewProvider.document!!.setText(
                """
                [command,set_value](hook ${"$"}value)
                {
                }
                """.trimIndent(),
            )
            PsiDocumentManager.getInstance(project).commitAllDocuments()
        }

        assertNotNull(injectedFileOrNull(file))
    }

    private fun addHookCommand() {
        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,set_hook](hook ${"$"}hook)
            {
            }
            """.trimIndent(),
        )
    }

    private fun configure(expression: String): PsiFile =
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,main]
            {
                $expression
            }
            """.trimIndent(),
        )

    private fun injectedFile(file: PsiFile): PsiElement =
        requireNotNull(injectedFileOrNull(file)) { "Expected a RuneScript hook injection" }

    private fun injectedFileOrNull(file: PsiFile): PsiElement? {
        val host = PsiTreeUtil.findChildOfType(file, RsStringLiteralContent::class.java) ?: return null
        val injectedFiles = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(host) ?: return null
        return injectedFiles.single().first
    }
}
