package io.runescript.plugin.ide

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.util.parentOfType
import com.intellij.ui.ColorChooserService
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.picker.ColorListener
import com.intellij.util.ui.ColorIcon
import io.runescript.plugin.ide.parameter.RsParameterBehaviorResolver
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
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
        return createMarker(element, color, colorProvider::setColorTo)
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>,
    ) {
        for (expression in elements.filterIsInstance<RsIntegerLiteralExpression>()) {
            if (RsParameterBehaviorResolver.find(expression, COLOR_BEHAVIOR_ID) == null) {
                continue
            }
            val color = parseRgb(expression.text) ?: continue
            result += createMarker(expression.firstChild, color, ::setRgbColor)
        }
    }

    private fun createMarker(
        element: PsiElement,
        color: Color,
        setColor: (PsiElement, Color) -> Unit,
    ): LineMarkerInfo<PsiElement> {
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
                            setColor(currentTag, selectedColor)
                        }
                    }
                }
            },
            GutterIconRenderer.Alignment.LEFT,
            { "Choose Color" },
        )
    }

    private fun setRgbColor(
        element: PsiElement,
        color: Color,
    ) {
        val expression = element as? RsIntegerLiteralExpression ?: element.parentOfType<RsIntegerLiteralExpression>() ?: return
        val replacement = "0x%06x".format(color.rgb and MAX_RGB)
        expression.replace(RsElementGenerator.createIntegerLiteral(element.project, replacement))
    }

    private fun parseRgb(text: String): Color? {
        val value =
            if (text.startsWith("0x", ignoreCase = true)) {
                text.substring(2).toIntOrNull(16)
            } else {
                text.toIntOrNull()
            } ?: return null
        return value.takeIf { it in 0..MAX_RGB }?.let(::Color)
    }

    private companion object {
        const val COLOR_BEHAVIOR_ID = "color"
        const val MAX_RGB = 0xffffff
    }
}
