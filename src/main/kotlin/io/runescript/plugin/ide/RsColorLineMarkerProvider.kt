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
    internal var showColorPicker: (Project, Color, RelativePoint, Boolean, (Color) -> Unit) -> Unit =
        { project, color, point, showAlpha, onChanged ->
            ColorChooserService.getInstance().showPopup(
                project,
                color,
                ColorListener { selectedColor, _ -> onChanged(selectedColor) },
                point,
                showAlpha,
            )
        }

    private val colorProvider = RsElementColorProvider()

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? {
        val color = colorProvider.getColorFrom(element) ?: return null
        return createMarker(element, color, false, colorProvider::setColorTo)
    }

    override fun collectSlowLineMarkers(
        elements: MutableList<out PsiElement>,
        result: MutableCollection<in LineMarkerInfo<*>>,
    ) {
        for (expression in elements.filterIsInstance<RsIntegerLiteralExpression>()) {
            val behavior = RsParameterBehaviorResolver.find(expression, COLOR_BEHAVIOR_IDS) ?: continue
            val format = ColorFormat.fromBehaviorId(behavior.id) ?: continue
            val color = format.parse(expression.text) ?: continue
            result +=
                createMarker(expression.firstChild, color, format.showAlpha) { element, selectedColor ->
                    setIntegerColor(element, selectedColor, format)
                }
        }
    }

    private fun createMarker(
        element: PsiElement,
        color: Color,
        showAlpha: Boolean,
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
                showColorPicker(project, color, RelativePoint(event), showAlpha) { selectedColor ->
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

    private fun setIntegerColor(
        element: PsiElement,
        color: Color,
        format: ColorFormat,
    ) {
        val expression = element as? RsIntegerLiteralExpression ?: element.parentOfType<RsIntegerLiteralExpression>() ?: return
        val replacement = format.format(color)
        expression.replace(RsElementGenerator.createIntegerLiteral(element.project, replacement))
    }

    private companion object {
        val COLOR_BEHAVIOR_IDS = ColorFormat.entries.mapTo(mutableSetOf(), ColorFormat::behaviorId)
    }

    private enum class ColorFormat(
        val behaviorId: String,
        val showAlpha: Boolean,
        private val digits: Int,
        private val maxValue: Long,
    ) {
        RGB("rgb", false, 6, 0xffffff),
        ARGB("argb", true, 8, 0xffffffffL),
        ;

        fun parse(text: String): Color? {
            val value =
                if (text.startsWith("0x", ignoreCase = true)) {
                    text.substring(2).toLongOrNull(16)
                } else {
                    text.toLongOrNull()
                } ?: return null
            if (value !in 0..maxValue) return null
            return when (this) {
                RGB -> {
                    Color(value.toInt())
                }

                ARGB -> {
                    Color(value.toInt(), true)
                }
            }
        }

        fun format(color: Color): String {
            val value =
                when (this) {
                    RGB -> {
                        color.rgb.toLong() and 0xffffff
                    }

                    ARGB -> {
                        color.rgb.toLong() and 0xffffffffL
                    }
                }
            return "0x%0${digits}x".format(value)
        }

        companion object {
            fun fromBehaviorId(id: String): ColorFormat? = entries.firstOrNull { format -> format.behaviorId == id }
        }
    }
}
