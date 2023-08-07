package io.runescript.plugin.ide

import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsElementTypes.STRING_TAG
import java.awt.Color
import java.util.regex.Pattern

class RsElementColorProvider : ElementColorProvider {

    override fun getColorFrom(element: PsiElement): Color? {
        val color = findColorFromTag(element) ?: return null
        return Color(color)
    }

    override fun setColorTo(element: PsiElement, color: Color) {
        val tagName = element.text.substring(1, element.text.indexOf('='))
        val document = PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile);
        CommandProcessor.getInstance().executeCommand(element.project, {
            element.replace(RsElementGenerator.createColorTag(element.project, color.rgb, tagName))
        }, "Change Color", null, document)
    }

    companion object {

        private val TAG_WITH_COLOR_PATTERN = Pattern.compile("<(?:col|shad|str|u)=([0-9a-fA-F]+)>")

        private fun findColorFromTag(element: PsiElement): Int? {
            if (element.elementType != STRING_TAG) {
                return null
            }
            val text = element.text
            val matcher = TAG_WITH_COLOR_PATTERN.matcher(text)
            if (!matcher.matches()) {
                return null
            }
            return matcher.group(1).toIntOrNull(16)
        }
    }
}