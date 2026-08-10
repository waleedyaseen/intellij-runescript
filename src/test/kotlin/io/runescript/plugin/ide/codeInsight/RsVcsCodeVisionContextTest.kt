package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.codeVision.settings.CodeVisionSettingsPreviewLanguage
import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScript

class RsVcsCodeVisionContextTest : BasePlatformTestCase() {
    fun testAcceptsScriptsAndExcludesNestedDeclarations() {
        myFixture.configureByText(
            "main.cs2",
            """
            [proc,target](int ${"$"}value)
            {
                foo(${"$"}value);
            }
            """.trimIndent(),
        )
        val script = script()
        val context = RsVcsCodeVisionContext()

        assertTrue(context.isAccepted(script))
        assertFalse(context.isAccepted(script.parameterList!!.parameterList.single()))
        assertFalse(context.isAccepted(script.statementList))
    }

    fun testUsesScriptNameThroughLastBodyElementAsBlameRange() {
        myFixture.configureByText(
            "main.cs2",
            """
            /** Documentation is outside the blame range. */
            [proc,target]
            {
                foo(1);
            }
            """.trimIndent(),
        )
        val script = script()

        val range = RsVcsCodeVisionContext().computeEffectiveRange(script)

        assertEquals(script.nameIdentifier!!.textRange.startOffset, range.startOffset)
        assertTrue(range.endOffset <= script.textRange.endOffset)
        assertTrue(range.endOffset > script.statementList.textRange.startOffset)
    }

    fun testRegistersVcsContextAndSettingsPreview() {
        val context = VcsCodeVisionLanguageContext.providersExtensionPoint.forLanguage(RuneScript)
        val previews =
            CodeVisionSettingsPreviewLanguage.EP_NAME.extensionList.filter { preview ->
                preview.modelId == "vcs.code.vision" && preview.language == "RuneScript"
            }

        assertTrue(context is RsVcsCodeVisionContext)
        assertEquals(1, previews.size)
        val bundle = previews.single().findBundle()
        assertNotNull(bundle)
        assertEquals(
            "The author who last edited most of a RuneScript script, according to the VCS history.",
            bundle!!.getString("RuneScript.codeLens.vcs.code.vision.description"),
        )
        val preview =
            RsVcsCodeVisionContext::class.java.classLoader
                .getResourceAsStream("codeVisionProviders/vcs.code.vision/preview.cs2")
                ?.bufferedReader()
                ?.use { it.readText() }
        assertNotNull(preview)
        assertTrue(preview!!, preview.contains("[proc,calculate_total]"))
    }

    private fun script(): RsScript = PsiTreeUtil.findChildOfType(myFixture.file, RsScript::class.java)!!
}
