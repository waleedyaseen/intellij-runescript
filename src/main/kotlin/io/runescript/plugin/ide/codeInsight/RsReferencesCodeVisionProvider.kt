package io.runescript.plugin.ide.codeInsight

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.hints.codeVision.ReferencesCodeVisionProvider
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.CachedValuesManager
import io.runescript.plugin.lang.psi.RsFile
import io.runescript.plugin.lang.psi.RsScript
import java.awt.event.MouseEvent

class RsReferencesCodeVisionProvider : ReferencesCodeVisionProvider() {
    override fun acceptsFile(file: PsiFile): Boolean = file is RsFile

    override fun acceptsElement(element: PsiElement): Boolean = element is RsScript

    override fun getHint(
        element: PsiElement,
        file: PsiFile,
    ): String? {
        val script = element as? RsScript ?: return null
        val count =
            CachedValuesManager.getProjectPsiDependentCache(script) { target ->
                ReferencesSearch.search(target).findAll().size
            }
        return when (count) {
            0 -> "no usages"
            1 -> "1 usage"
            else -> "$count usages"
        }
    }

    override val relativeOrderings: List<CodeVisionRelativeOrdering> =
        listOf(CodeVisionRelativeOrdering.CodeVisionRelativeOrderingFirst)

    override val id: String = ID

    override fun handleClick(
        editor: Editor,
        element: PsiElement,
        event: MouseEvent?,
    ) {
        val script = element as? RsScript ?: return
        super.handleClick(editor, script, event)
    }

    companion object {
        const val ID = "runescript.references"
    }
}
