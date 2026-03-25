package io.runescript.plugin.ide.icons

import com.intellij.ide.IconProvider
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.mixin.getIconForTriggerName
import io.runescript.plugin.lang.psi.triggerName
import javax.swing.Icon

class RsIconProvider : IconProvider() {
    override fun getIcon(
        element: PsiElement,
        flags: Int,
    ): Icon? {
        if (element is RsScript) {
            return getIconForTriggerName(element.triggerName)
        }
        return null
    }
}
