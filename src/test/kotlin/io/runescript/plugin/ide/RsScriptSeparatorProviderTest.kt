package io.runescript.plugin.ide

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsScriptSeparatorProviderTest : RsParserTestCase() {
    fun testSeparatesEveryScriptAfterFirstWhenEnabled() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,first]
                return;

                /** second */
                [proc,second]
                return;
                """.trimIndent(),
            )
        val scripts = PsiTreeUtil.getChildrenOfTypeAsList(file, RsScript::class.java)
        val settings = DaemonCodeAnalyzerSettings.getInstance()
        val original = settings.SHOW_METHOD_SEPARATORS
        try {
            settings.SHOW_METHOD_SEPARATORS = true
            val provider = RsScriptSeparatorProvider()
            val firstLeaf = PsiTreeUtil.getDeepestFirst(scripts[0])
            val secondLeaf = PsiTreeUtil.getDeepestFirst(scripts[1])
            assertNull(provider.getLineMarkerInfo(firstLeaf))
            assertNotNull(provider.getLineMarkerInfo(secondLeaf))

            settings.SHOW_METHOD_SEPARATORS = false
            assertNull(provider.getLineMarkerInfo(secondLeaf))
        } finally {
            settings.SHOW_METHOD_SEPARATORS = original
        }
    }
}
