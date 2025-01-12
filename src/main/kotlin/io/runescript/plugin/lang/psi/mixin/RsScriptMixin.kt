package io.runescript.plugin.lang.psi.mixin

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.startOffset
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.codeInsight.controlFlow.RsControlFlow
import io.runescript.plugin.ide.codeInsight.controlFlow.RsControlFlowBuilder
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.stubs.RsScriptStub
import javax.swing.Icon

abstract class RsScriptMixin : StubBasedPsiElementBase<RsScriptStub>, RsScript {

    constructor(node: ASTNode) : super(node)
    constructor(stub: RsScriptStub, type: IStubElementType<*, *>) : super(stub, type)
    constructor(stub: RsScriptStub?, type: IElementType?, node: ASTNode?) : super(stub, type, node)

    override val controlFlow: RsControlFlow
        get() = CachedValuesManager.getCachedValue(this) {
            val builder = RsControlFlowBuilder()
            val result = builder.build(this)
            val controlFlow = RsControlFlow(result.instructions)
            CachedValueProvider.Result(controlFlow, this)
        }

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ): Boolean {
        parameterList?.let {
            if (!it.processDeclarations(processor, state, lastParent, place)) {
                return false
            }
        }
        return true
    }

    override fun getPresentation(): ItemPresentation? {
        val icon = getIconForTriggerName(triggerName)
        return PresentationData(qualifiedName, containingFile.name, icon, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
    }

    override fun getUseScope(): SearchScope {
        val module = ModuleUtil.findModuleForPsiElement(this) ?: return super.getUseScope()
        return GlobalSearchScope.moduleScope(module)
    }

    override fun setName(name: String): PsiElement {
        return RsPsiImplUtil.setName(scriptNameExpression, name)
    }

    override fun getName(): String? {
        return RsPsiImplUtil.getName(scriptNameExpression)
    }

    override fun getNameIdentifier(): PsiElement? {
        return scriptNameExpression
    }

    override fun getTextOffset(): Int {
        return scriptNameExpression.startOffset
    }
}

fun getIconForTriggerName(triggerName: String): Icon {
    return when (triggerName) {
        "proc" -> RsIcons.GutterProc
        "clientscript" -> RsIcons.GutterClientScript
        "command" -> RsIcons.GutterCommand
        else -> RsIcons.GutterOther
    }
}