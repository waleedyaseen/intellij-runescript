package io.runescript.plugin.ide

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import java.awt.Color
import java.awt.event.MouseEvent
import javax.swing.JPanel

class RsColorLineMarkerProviderTest : RsParserTestCase() {
    fun testDisplaysColorsForDocAnnotatedCommandParameters() {
        myFixture.addFileToProject(
            "commands.cs2",
            """
            /**
             * @parammeta x1 rgb
             */
            [command,cc_setgraphicshadow](int ${"$"}x1)

            /**
             * @parammeta x1 argb
             */
            [command,.cc_setgraphicshadow](int ${"$"}x1)

            [command,plain_int](int ${"$"}x1)
            """.trimIndent(),
        )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    cc_setgraphicshadow(0x112233);
                    .cc_setgraphicshadow(0x80445566);
                    plain_int(0x778899);
                }
                """.trimIndent(),
            )
        val provider = RsColorLineMarkerProvider()
        val markers = mutableListOf<com.intellij.codeInsight.daemon.LineMarkerInfo<*>>()

        provider.collectSlowLineMarkers(PsiTreeUtil.collectElements(file) { true }.toMutableList(), markers)

        assertSize(2, markers)
        assertEquals(
            listOf("0x112233", "0x80445566"),
            markers.mapNotNull { marker -> marker.element?.text },
        )

        val alphaModes = mutableListOf<Boolean>()
        val initialColors = mutableListOf<Color>()
        provider.showColorPicker = { _, initialColor, _, showAlpha, onChanged ->
            initialColors += initialColor
            alphaModes += showAlpha
            onChanged(Color(0x00, 0xff, 0x00, 0x7f))
        }
        navigate(markers.first())
        navigate(markers.last())
        assertEquals(listOf(false, true), alphaModes)
        assertEquals(listOf(0xff112233.toInt(), 0x80445566.toInt()), initialColors.map(Color::getRGB))
        assertTrue(file.text.contains("cc_setgraphicshadow(0x00ff00);"))
        assertTrue(file.text.contains(".cc_setgraphicshadow(0x7f00ff00);"))
    }

    fun testColorMarkerUsesCurrentPsiAfterReparse() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    debug("<col=ff0000>red</col>");
                }
                """.trimIndent(),
            )
        val originalTag = file.findElementAt(file.text.indexOf("<col="))!!
        val provider = RsColorLineMarkerProvider()
        val marker = provider.getLineMarkerInfo(originalTag)!!
        provider.showColorPicker = { _, _, _, _, onChanged -> onChanged(Color(0x00ff00)) }

        WriteCommandAction.runWriteCommandAction(project) {
            myFixture.editor.document.replaceString(
                originalTag.textRange.startOffset,
                originalTag.textRange.endOffset,
                "<col=ff0001>",
            )
            PsiDocumentManager.getInstance(project).commitDocument(myFixture.editor.document)
        }

        assertFalse(originalTag.isValid)
        val currentTag = marker.element
        assertNotNull(currentTag)
        assertTrue(currentTag!!.isValid)
        marker.navigationHandler.navigate(
            MouseEvent(JPanel(), MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 1, false),
            currentTag,
        )
        assertTrue(file.text.contains("<col=00ff00>"))
    }

    @Suppress("UNCHECKED_CAST")
    private fun navigate(marker: LineMarkerInfo<*>) {
        val typedMarker = marker as LineMarkerInfo<PsiElement>
        typedMarker.navigationHandler.navigate(
            MouseEvent(JPanel(), MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 1, false),
            typedMarker.element,
        )
    }
}
