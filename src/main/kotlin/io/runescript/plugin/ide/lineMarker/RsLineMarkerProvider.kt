package io.runescript.plugin.ide.lineMarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStringLiteralExpression

class RsLineMarkerProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (element is RsScript) {
            // TODO(Walied): Find a better way around icons and text
            val presentation = element.presentation!!
            return NavigationGutterIconBuilder.create(presentation.getIcon(false)!!)
                    .setTarget(element)
                    .setTooltipText("Navigate to ${presentation.presentableText}")
                    .createLineMarkerInfo(element)
        }
        return null
    }
}