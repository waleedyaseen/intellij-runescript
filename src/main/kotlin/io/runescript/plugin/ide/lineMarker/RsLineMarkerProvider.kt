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
            val targetElement = element.scriptHeader.scriptName.nameExpression!!.identifier!!
            val presentation = element.presentation!!
            return NavigationGutterIconBuilder.create(presentation.getIcon(false)!!)
                    .setTarget(targetElement)
                    .setTooltipText("Navigate to ${presentation.presentableText}")
                    .createLineMarkerInfo(targetElement)
        }
        return null
    }
}