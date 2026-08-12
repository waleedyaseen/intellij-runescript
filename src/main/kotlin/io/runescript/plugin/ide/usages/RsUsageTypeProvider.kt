package io.runescript.plugin.ide.usages

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.usages.impl.rules.UsageType
import com.intellij.usages.impl.rules.UsageTypeProvider
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsHookFragment
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import java.util.function.Supplier

class RsUsageTypeProvider : UsageTypeProvider {
    override fun getUsageType(element: PsiElement): UsageType? {
        val local = parentOrSelf<RsLocalVariableExpression>(element)
        if (local != null) {
            return when (RsReadWriteAccessDetector().getExpressionAccess(local)) {
                Access.Read -> UsageType.READ
                Access.Write -> UsageType.WRITE
                Access.ReadWrite -> READ_WRITE
            }
        }
        if (parentOrSelf<RsDocName>(element) != null) return DOCUMENTATION
        if (parentOrSelf<RsHookFragment>(element) != null) return HOOK
        if (parentOrSelf<RsCallExpression>(element) != null) return CALL
        if (element.reference?.resolve() is RsSymSymbol) return SYMBOL
        return null
    }

    private inline fun <reified T : PsiElement> parentOrSelf(element: PsiElement): T? =
        (element as? T) ?: PsiTreeUtil.getParentOfType(element, T::class.java, false)

    companion object {
        val CALL = UsageType(Supplier { "Script call" })
        val HOOK = UsageType(Supplier { "Hook callback" })
        val DOCUMENTATION = UsageType(Supplier { "RSDoc reference" })
        val SYMBOL = UsageType(Supplier { "Symbol reference" })
        val READ_WRITE = UsageType(Supplier { "Read/write access" })
    }
}
