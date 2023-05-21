package io.runescript.plugin.lang.psi.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import io.runescript.plugin.lang.psi.RsGosubExpression

class RsGosubExpressionManipulator : AbstractElementManipulator<RsGosubExpression>() {

    override fun handleContentChange(element: RsGosubExpression, range: TextRange, newContent: String?): RsGosubExpression? {
        return element
    }
}