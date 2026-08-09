package io.runescript.plugin.ide

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import io.runescript.plugin.lang.parser.RsParserTestCase
import java.awt.Color
import java.awt.event.MouseEvent
import javax.swing.JPanel

class RsColorLineMarkerProviderTest : RsParserTestCase() {
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
        provider.showColorPicker = { _, _, _, onChanged -> onChanged(Color(0x00ff00)) }

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
}
