package io.runescript.plugin.ide.codeInsight.intention

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.psi.RsScript

internal fun PsiElement.findScriptAtSignature(): RsScript? {
    val script = this as? RsScript ?: PsiTreeUtil.getParentOfType(this, RsScript::class.java, false) ?: return null
    val headerEnd = (script.returnList ?: script.parameterList ?: script.rbracket).textRange.endOffset
    return script.takeIf {
        textRange.startOffset >= script.lbracket.textRange.startOffset && textRange.endOffset <= headerEnd
    }
}
