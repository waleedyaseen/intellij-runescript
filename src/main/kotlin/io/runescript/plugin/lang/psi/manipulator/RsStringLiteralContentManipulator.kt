package io.runescript.plugin.lang.psi.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsStringLiteralContent

class RsStringLiteralContentManipulator : AbstractElementManipulator<RsStringLiteralContent>() {

    override fun handleContentChange(element: RsStringLiteralContent, range: TextRange, newContent: String): RsStringLiteralContent? {
        val newElement = RsElementGenerator.createStringLiteralContent(element.project, newContent)
        element.replace(newElement)
        return newElement
    }
}