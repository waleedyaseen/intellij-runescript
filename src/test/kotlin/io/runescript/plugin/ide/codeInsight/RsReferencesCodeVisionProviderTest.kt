package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.codeVision.CodeVisionHost
import com.intellij.codeInsight.codeVision.settings.CodeVisionSettingsPreviewLanguage
import com.intellij.codeInsight.codeVision.ui.model.TextCodeVisionEntry
import com.intellij.codeInsight.daemon.impl.analysis.FileHighlightingSetting
import com.intellij.codeInsight.daemon.impl.analysis.HighlightingSettingsPerFile
import com.intellij.codeInsight.hints.InlayHintsUtils
import com.intellij.codeInsight.hints.codeVision.DaemonBoundCodeVisionProvider
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.TestModeFlags
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scriptName
import io.runescript.plugin.lang.psi.scriptNameExpression
import io.runescript.plugin.lang.psi.triggerNameExpression

class RsReferencesCodeVisionProviderTest : BasePlatformTestCase() {
    private val provider = RsReferencesCodeVisionProvider()

    fun testCountsProjectReferences() {
        configure(
            """
            [proc,target]
            {
            }

            [proc,caller]
            {
                ~target;
                ~target();
            }
            """,
        )

        assertEquals("2 usages", provider.getHint(script("target"), myFixture.file))
        assertEquals("no usages", provider.getHint(script("caller"), myFixture.file))
    }

    fun testOnlyAcceptsScriptsRatherThanBodyElements() {
        configure(
            """
            [proc,target]
            {
                foo(1);
            }
            """,
        )
        val script = script("target")

        assertTrue(provider.acceptsFile(myFixture.file))
        assertTrue(provider.acceptsElement(script))
        assertFalse(provider.acceptsElement(script.scriptNameExpression))
        assertFalse(provider.acceptsElement(script.statementList))
        assertFalse(provider.acceptsElement(script.statementList.firstChild))
    }

    fun testAnchorElementIsScriptSignatureNotDocumentationOrBody() {
        configure(
            """
            /** Documentation above the lens. */
            [proc,target]
            {
            }
            """,
        )
        val script = script("target")

        val signatureRange = InlayHintsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(script)

        assertEquals(myFixture.file.text.indexOf("[proc,target]"), signatureRange.startOffset)
        assertTrue(InlayHintsUtils.isFirstInLine(script))
        assertTrue(provider.acceptsElement(script))
        assertFalse(provider.acceptsElement(script.triggerNameExpression))
        assertFalse(provider.acceptsElement(script.statementList))
    }

    fun testProducesEntriesThroughCodeVisionPipeline() {
        configure(
            """
            [proc,target]
            {
            }

            [proc,caller]
            {
                ~target;
            }
            """,
        )
        val testKey = CodeVisionHost.isCodeVisionTestKey
        val highlightingSettings = HighlightingSettingsPerFile.getInstance(project)
        TestModeFlags.set(testKey, true)
        highlightingSettings.setHighlightingSettingForRoot(myFixture.file, FileHighlightingSetting.FORCE_HIGHLIGHTING)

        try {
            assertTrue(CodeVisionHost.isCodeLensTest())
            assertEquals(FileHighlightingSetting.FORCE_HIGHLIGHTING, highlightingSettings.getHighlightingSettingForRoot(myFixture.file))
            assertFalse(ProjectFileIndex.getInstance(project).isInLibrarySource(myFixture.file.virtualFile))
            assertTrue(PsiTreeUtil.findChildrenOfType(myFixture.file, RsScript::class.java).all(InlayHintsUtils::isFirstInLine))
            val entries = provider.computeForEditor(myFixture.editor, myFixture.file)

            assertEquals(2, entries.size)
            assertEquals(listOf("1 usage", "no usages"), entries.map { (_, entry) -> (entry as TextCodeVisionEntry).text })
            assertEquals(
                listOf(myFixture.file.text.indexOf("[proc,target]"), myFixture.file.text.indexOf("[proc,caller]")),
                entries.map { (range, _) -> range.startOffset },
            )
        } finally {
            TestModeFlags.reset(testKey)
        }
    }

    fun testRegistersExactlyOneRuneScriptReferencesProvider() {
        val providers =
            DaemonBoundCodeVisionProvider.Companion.extensionPoint.extensionList
                .filterIsInstance<RsReferencesCodeVisionProvider>()

        assertEquals(1, providers.size)
    }

    fun testRegistersRuneScriptUsagesSettingsPreview() {
        val previews =
            CodeVisionSettingsPreviewLanguage.EP_NAME.extensionList.filter { preview ->
                preview.modelId == "references" && preview.language == "RuneScript"
            }

        assertEquals(1, previews.size)
        val bundle = previews.single().findBundle()
        assertNotNull(bundle)
        assertEquals(
            "The number of usages of a RuneScript script in the project. Click the hint to navigate to its usages.",
            bundle!!.getString("RuneScript.codeLens.references.description"),
        )
        val preview =
            RsReferencesCodeVisionProvider::class.java.classLoader
                .getResourceAsStream("codeVisionProviders/references/preview.cs2")
                ?.bufferedReader()
                ?.use { it.readText() }
        assertNotNull(preview)
        assertTrue(preview!!, preview.contains("[proc,calculate_total]"))
        assertTrue(preview, preview.contains("~calculate_total(1);"))
    }

    private fun configure(text: String) {
        myFixture.configureByText("main.cs2", text.trimIndent())
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
                longSupport = true,
            ),
        )
    }

    private fun script(name: String): RsScript =
        PsiTreeUtil.findChildrenOfType(myFixture.file, RsScript::class.java).first { script -> script.scriptName == name }
}
