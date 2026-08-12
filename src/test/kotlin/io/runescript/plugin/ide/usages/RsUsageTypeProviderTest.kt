package io.runescript.plugin.ide.usages

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.usages.impl.rules.UsageType
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsStringLiteralContent

class RsUsageTypeProviderTest : RsParserTestCase() {
    private lateinit var provider: RsUsageTypeProvider

    override fun setUp() {
        super.setUp()
        provider = RsUsageTypeProvider()
    }

    fun testClassifiesCallsHooksAndDocumentation() {
        myFixture.addFileToProject("commands.cs2", "[command,set_hook](hook ${'$'}hook)\nreturn;")
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Calls [proc/target]. */
                [proc,main]
                {
                    ~target();
                    set_hook("target");
                }
                """.trimIndent(),
            )
        val call = PsiTreeUtil.findChildOfType(file, RsGosubExpression::class.java)!!
        val host = PsiTreeUtil.findChildOfType(file, RsStringLiteralContent::class.java)!!
        val injected =
            InjectedLanguageManager
                .getInstance(project)
                .getInjectedPsiFiles(host)!!
                .single()
                .first
        val hook = PsiTreeUtil.findChildOfType(injected, RsHookFragment::class.java)!!
        val doc = PsiTreeUtil.findChildrenOfType(file, RsDocName::class.java).single { it.parent !is RsDocName }

        assertEquals(RsUsageTypeProvider.CALL, provider.getUsageType(call.nameIdentifier!!))
        assertEquals(RsUsageTypeProvider.HOOK, provider.getUsageType(hook))
        assertEquals(RsUsageTypeProvider.DOCUMENTATION, provider.getUsageType(doc))
    }

    fun testClassifiesVariableAccess() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${'$'}value = 1;
                    ${'$'}value = ${'$'}value;
                    ${'$'}value++;
                }
                """.trimIndent(),
            )
        val usages =
            PsiTreeUtil
                .findChildrenOfType(file, RsLocalVariableExpression::class.java)
                .filter { it.reference?.resolve() != it }
                .drop(1)

        assertEquals(UsageType.WRITE, provider.getUsageType(usages[0]))
        assertEquals(UsageType.READ, provider.getUsageType(usages[1]))
        assertEquals(RsUsageTypeProvider.READ_WRITE, provider.getUsageType(usages[2]))
    }
}
