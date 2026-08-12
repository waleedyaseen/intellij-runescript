package io.runescript.plugin.ide

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.impl.LineMarkersPass
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsScript

class RsScriptSeparatorProvider : LineMarkerProvider {
    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!DaemonCodeAnalyzerSettings.getInstance().SHOW_METHOD_SEPARATORS) return null
        val script = PsiTreeUtil.getParentOfType(element, RsScript::class.java, false) ?: return null
        if (element !== PsiTreeUtil.getDeepestFirst(script) || script.previousScript() == null) return null
        return LineMarkersPass.createMethodSeparatorLineMarker(element, EditorColorsManager.getInstance())
    }

    private fun RsScript.previousScript(): RsScript? {
        var sibling = prevSibling
        while (sibling != null) {
            if (sibling is RsScript) return sibling
            sibling = sibling.prevSibling
        }
        return null
    }
}
