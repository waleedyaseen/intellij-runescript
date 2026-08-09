package io.runescript.plugin.ide

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.ui.ColorChooserService
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.picker.ColorListener
import com.intellij.util.ui.ColorIcon
import java.awt.Color

class RsColorLineMarkerProvider : LineMarkerProvider {
    internal var showColorPicker: (Project, Color, RelativePoint, (Color) -> Unit) -> Unit = { project, color, point, onChanged ->
        ColorChooserService.getInstance().showPopup(
            project,
            color,
            ColorListener { selectedColor, _ -> onChanged(selectedColor) },
            point,
            true,
        )
    }

    private val colorProvider = RsElementColorProvider()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        val color = colorProvider.getColorFrom(element) ?: return null
        val project = element.project
        return LineMarkerInfo(
            element,
            element.textRange,
            ColorIcon(12, color),
            { "Choose color" },
            { event, currentElement ->
                if (!currentElement.isValid || !currentElement.isWritable) {
                    return@LineMarkerInfo
                }
                val pointer = SmartPointerManager.createPointer(currentElement)
                showColorPicker(project, color, RelativePoint(event)) { selectedColor ->
                    WriteCommandAction.runWriteCommandAction(project) {
                        val currentTag = pointer.element
                        if (currentTag != null && currentTag.isValid) {
                            colorProvider.setColorTo(currentTag, selectedColor)
                        }
                    }
                }
            },
            GutterIconRenderer.Alignment.LEFT,
            { "Choose Color" },
        )
    }
}
