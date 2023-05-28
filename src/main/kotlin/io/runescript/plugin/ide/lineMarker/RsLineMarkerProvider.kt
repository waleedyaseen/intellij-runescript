package io.runescript.plugin.ide.lineMarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsScript

class RsLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is RsScript) {
            // TODO(Walied): Find a better way around icons and text
            val targetElement = element.scriptHeader.scriptName.nameExpression?.identifier ?: return null
            val presentation = element.presentation ?: return null
            val icon = presentation.getIcon(false) ?: return null
            return NavigationGutterIconBuilder.create(icon)
                    .setTarget(targetElement)
                    .setTooltipText("Navigate to ${presentation.presentableText}")
                    .createLineMarkerInfo(targetElement)
        }
        return null
    }
}