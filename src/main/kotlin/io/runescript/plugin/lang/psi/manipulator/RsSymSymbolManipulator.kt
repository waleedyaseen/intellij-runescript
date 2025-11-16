package io.runescript.plugin.lang.psi.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsSymSymbolManipulator : AbstractElementManipulator<RsSymSymbol>() {

    override fun handleContentChange(element: RsSymSymbol, range: TextRange, newContent: String?): RsSymSymbol? {
        if (newContent == null) return null
        val oldContent = element.text
        val newText = "${oldContent.take(range.startOffset)}$newContent${oldContent.substring(range.endOffset)}"
        val newElement = RsElementGenerator.createSymSymbol(element.project, newText)
        element.replace(newElement)
        return newElement

    }
}