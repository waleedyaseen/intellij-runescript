package io.runescript.plugin.lang.psi.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.psi.RsElementGenerator

class RsDocNameManipulator : AbstractElementManipulator<RsDocName>() {

    override fun handleContentChange(element: RsDocName, range: TextRange, newContent: String): RsDocName {
        val newElement = RsElementGenerator.createDocIdentifier(element.project, newContent)
        element.lastChild.replace(newElement)
        return element
    }
}