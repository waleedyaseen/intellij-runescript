package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.RefactoringActionHandler
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsNamedElement

abstract class RsRefactoringActionBase : RefactoringActionHandler {

    protected fun findTargetExpression(file: PsiFile, editor: Editor): RsExpression? {
        val selModel = editor.selectionModel
        if (selModel.hasSelection()) {
            val range = TextRange(selModel.selectionStart, selModel.selectionEnd)
            val start = file.findElementAt(range.startOffset)
            val end = file.findElementAt(range.endOffset - 1)
            val common = PsiTreeUtil.findCommonParent(start, end)
            val expr = PsiTreeUtil.getParentOfType(common, RsExpression::class.java, false)
            if (expr != null && expr.textRange.contains(range)) {
                return expr
            }
        }
        val element = file.findElementAt(editor.caretModel.offset) ?: return null
        return PsiTreeUtil.getParentOfType(element, RsExpression::class.java, false)
    }

    protected fun moveCaretToName(editor: Editor, namedElement: RsNamedElement) {
        val caret = editor.caretModel
        val start = namedElement.textOffset
        caret.moveToOffset(start)
        val len = namedElement.name?.length ?: 0
        editor.selectionModel.setSelection(start, start + len)
    }
}