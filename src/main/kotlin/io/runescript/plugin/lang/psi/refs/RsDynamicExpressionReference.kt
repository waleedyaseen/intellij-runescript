package io.runescript.plugin.lang.psi.refs

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.scope.RsLocalVariableResolver
import io.runescript.plugin.lang.psi.scope.RsResolveMode
import io.runescript.plugin.lang.psi.scope.RsScopesUtil
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.typeCheckedType
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex
import io.runescript.plugin.lang.stubs.index.RsScriptIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class RsDynamicExpressionReference(element: RsDynamicExpression) :
    PsiPolyVariantReferenceBase<RsDynamicExpression>(element, element.nameLiteral.textRangeInParent) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return resolveElement(element, element.typeCheckedType)
    }

    override fun getVariants(): Array<out LookupElement> = LookupElement.EMPTY_ARRAY

    override fun handleElementRename(newElementName: String): PsiElement {
        return element.setName(newElementName)
    }

    companion object {
        fun resolveElement(element: RsDynamicExpression, type: Type): Array<ResolveResult> {
            val elementName = element.nameLiteral.text

            // Try to resolve the element as a local array reference.
            val localArrayResolver = RsLocalVariableResolver(elementName, RsResolveMode.Arrays)
            RsScopesUtil.walkUpScopes(localArrayResolver, ResolveState.initial(), element)
            val localArray = localArrayResolver.declaration
            if (localArray != null) {
                return arrayOf(PsiElementResolveResult(localArray))
            }

            // Try to resolve the element as a script reference.
            val triggerType = getScriptTriggerForType(type)
            if (triggerType != null) {
                val project = element.project
                val module = ModuleUtil.findModuleForPsiElement(element) ?: return emptyArray()
                val searchScope = GlobalSearchScope.moduleScope(module)
                return StubIndex.getElements(
                    RsScriptIndex.KEY,
                    "[${triggerType.identifier},$elementName]",
                    project,
                    searchScope,
                    RsScript::class.java
                ).map { PsiElementResolveResult(it) }.toTypedArray()
            }

            // Try to resolve the element as a config reference.
            val project = element.project
            val resolvedConfig = RsSymbolIndex.lookup(element, type, elementName)
            if (resolvedConfig != null) {
                return arrayOf(PsiElementResolveResult(resolvedConfig))
            }

            // Try to resolve the element as a command reference.
            val module = ModuleUtil.findModuleForPsiElement(element) ?: return emptyArray()
            val searchScope = GlobalSearchScope.moduleScope(module)

            return StubIndex.getElements(
                RsCommandScriptIndex.KEY,
                elementName,
                project,
                searchScope,
                RsScript::class.java
            ).map { PsiElementResolveResult(it) }.toTypedArray()
        }

        private fun getScriptTriggerForType(type: Type): TriggerType? {
            return when (type) {
                is MetaType.Script -> type.trigger
                else -> null
            }
        }
    }
}