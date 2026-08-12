package io.runescript.plugin.ide.formatter.arrangement

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.arrangement.Rearranger
import com.intellij.psi.codeStyle.arrangement.engine.ArrangementEngine
import com.intellij.psi.codeStyle.arrangement.match.StdArrangementEntryMatcher
import com.intellij.psi.codeStyle.arrangement.match.StdArrangementMatchRule
import com.intellij.psi.codeStyle.arrangement.model.ArrangementAtomMatchCondition
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementSettings
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokens
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.parser.RsParserTestCase
import org.jdom.Element

class RsRearrangerTest : RsParserTestCase() {
    fun testRegisteredForRuneScript() {
        assertInstanceOf(Rearranger.EXTENSION.forLanguage(RuneScript), RsRearranger::class.java)
    }

    fun testPersistsCustomScriptRules() {
        val rearranger = RsRearranger()
        val settings =
            StdArrangementSettings.createByMatchRules(
                emptyList(),
                listOf(
                    StdArrangementMatchRule(
                        StdArrangementEntryMatcher(ArrangementAtomMatchCondition(RsRearranger.SCRIPT)),
                        StdArrangementTokens.Order.KEEP,
                    ),
                ),
            )
        val element = Element("arrangement")

        rearranger.serializer.serialize(settings, element)
        val restored = rearranger.serializer.deserialize(element)

        assertEquals(settings, restored)
    }

    fun testRearrangesScriptsByQualifiedName() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,zebra]
                return;

                /** client docs */
                [clientscript,alpha]
                return;

                [proc,alpha]
                return;
                """.trimIndent(),
            )
        val settings = CodeStyle.createTestSettings()
        val rearranger = RsRearranger()
        settings.getCommonSettings(RuneScript).apply {
            BLANK_LINES_AROUND_METHOD = 1
            setArrangementSettings(rearranger.defaultSettings)
        }

        CodeStyle.doWithTemporarySettings(
            project,
            settings,
            Runnable {
                WriteCommandAction.runWriteCommandAction(project) {
                    ArrangementEngine.getInstance().arrange(file, listOf(file.textRange))
                    PsiDocumentManager.getInstance(project).commitDocument(myFixture.editor.document)
                }
            },
        )

        assertEquals(
            """
            /** client docs */
            [clientscript,alpha]
            return;

            [proc,alpha]
            return;

            [proc,zebra]
            return;
            """.trimIndent(),
            file.text,
        )
    }

    fun testOnlyParsesScriptsInsideRequestedRange() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,first]
                return;

                [proc,second]
                return;
                """.trimIndent(),
            )
        val secondOffset = file.text.indexOf("[proc,second]")

        val entries =
            RsRearranger().parse(
                file,
                myFixture.editor.document,
                mutableListOf(TextRange(secondOffset, file.textLength)),
                RsRearranger().defaultSettings,
            )

        assertEquals(listOf("[proc,second]"), entries.map { it.name })
    }
}
