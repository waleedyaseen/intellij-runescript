package io.runescript.plugin.symbollang.psi.index

import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.lang.psi.typechecker.type.*
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.VarBitType
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.VarPlayerType
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.resolveToSymTypeName
import io.runescript.plugin.symbollang.psi.stub.types.RsSymFileStubType
import java.util.*


class RsSymbolIndex : StringStubIndexExtension<RsSymSymbol>() {

    override fun getVersion(): Int = RsSymFileStubType.stubVersion

    override fun getKey(): StubIndexKey<String, RsSymSymbol> = KEY

    companion object {
        val KEY =
            StubIndexKey.createIndexKey<String, RsSymSymbol>("io.runescript.plugin.symbollang.psi.index.RsSymbolIndex")

        fun lookup(context: PsiElement, type: Type, name: String): RsSymSymbol? {
            val literal = when (type) {
                ScriptVarType.NAMEDOBJ -> "obj"
                is IfScriptType -> "if_script"
                is DbColumnType -> "dbcolumn"
                is VarPlayerType -> "varp"
                is VarBitType -> "varbit"
                is ParamType -> "param"
                is PrimitiveType, is ScriptVarType -> type.representation
                else -> return null
            }
            return lookup(context, literal, name)
        }

        fun lookup(context: PsiElement, literal: String, name: String): RsSymSymbol? {
            val module = ModuleUtil.findModuleForPsiElement(context) ?: return null
            val scope = GlobalSearchScope.moduleScope(module)
            val configs = StubIndex.getElements(KEY, name, context.project, scope, RsSymSymbol::class.java)
            return configs.singleOrNull {
                resolveToSymTypeName(it.containingFile) == literal
            }
        }

        fun lookupAll(context: PsiElement, name: String): Collection<RsSymSymbol> {
            val module = ModuleUtil.findModuleForPsiElement(context) ?: return Collections.emptyList()
            val scope = GlobalSearchScope.moduleScope(module)
            return StubIndex.getElements(KEY, name, context.project, scope, RsSymSymbol::class.java)
        }
    }
}
