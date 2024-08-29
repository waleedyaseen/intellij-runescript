package io.runescript.plugin.ide.doc

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.stubs.index.RsClientScriptIndex
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex.Companion.nameWithoutExtension

class RsDocReference(element: RsDocName) : PsiPolyVariantReferenceBase<RsDocName>(element) {

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val typeName = (element.firstChild as? RsDocName)?.text
        val elementName = element.lastChild.text
        val module = ModuleUtil.findModuleForPsiElement(element) ?: return emptyArray()
        return resolve(element.project, module, element.getContainingDoc().owner, typeName, elementName)
            .map { PsiElementResolveResult(it) }
            .toTypedArray()
    }

    override fun getVariants(): Array<Any> = emptyArray()

    override fun isSoft() = false

    override fun getRangeInElement(): TextRange {
        return element.getNameTextRange()
    }

    override fun getCanonicalText(): String {
        return element.getNameText()
    }

    companion object {
        fun resolve(project: Project, module: Module, owner: RsScript?, typeName: String?, elementName: String): Array<PsiElement> {
            if (typeName == null) {
                return owner
                    ?.parameterList
                    ?.parameterList
                    ?.map { it.localVariableExpression }
                    ?.filter { it.nameLiteral.text == elementName }
                    ?.toTypedArray()
                    ?: emptyArray()
            }
            val scriptIndexKey = when (typeName) {
                "command" -> RsCommandScriptIndex.KEY
                "clientscript" -> RsClientScriptIndex.KEY
                "proc" -> RsProcScriptIndex.KEY
                else -> null
            }
            if (scriptIndexKey != null) {
                val scripts = StubIndex.getElements(
                    scriptIndexKey,
                    elementName,
                    project,
                    GlobalSearchScope.moduleScope(module),
                    RsScript::class.java
                )
                return scripts.toTypedArray()
            }
            val configs = StubIndex.getElements(
                RsSymbolIndex.KEY,
                elementName,
                project,
                GlobalSearchScope.moduleScope(module),
                RsSymSymbol::class.java
            )
            return configs
                .filter { it.containingFile.nameWithoutExtension == typeName }
                .toTypedArray()
        }
    }
}