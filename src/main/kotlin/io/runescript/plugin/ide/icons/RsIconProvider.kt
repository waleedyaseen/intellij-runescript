package io.runescript.plugin.ide.icons

import com.intellij.ide.FileIconProvider
import com.intellij.ide.IconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.neptune.isNeptuneBuildFile
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.mixin.getIconForTriggerName
import io.runescript.plugin.lang.psi.triggerName
import javax.swing.Icon

class RsIconProvider : IconProvider() {
    override fun getIcon(element: PsiElement, flags: Int): Icon? {
        if (element is RsScript) {
            return getIconForTriggerName(element.triggerName)
        }
        return null
    }
}