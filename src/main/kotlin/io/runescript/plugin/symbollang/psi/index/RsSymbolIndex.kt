package io.runescript.plugin.symbollang.psi.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.symbollang.psi.RsSymSymbol
import io.runescript.plugin.symbollang.psi.stub.types.RsSymFileStubType


class RsSymbolIndex : StringStubIndexExtension<RsSymSymbol>() {

    override fun getVersion(): Int = RsSymFileStubType.stubVersion

    override fun getKey(): StubIndexKey<String, RsSymSymbol> = KEY

    companion object {
        val KEY = StubIndexKey.createIndexKey<String, RsSymSymbol>("io.runescript.plugin.symbollang.psi.index.RsSymbolIndex")

        fun lookup(project: Project, type: RsPrimitiveType, name: String): RsSymSymbol? {
            val lookupType = if (type == RsPrimitiveType.NAMEDOBJ) RsPrimitiveType.OBJ else type
            val scope = GlobalSearchScope.allScope(project)
            val configs = StubIndex.getElements(KEY, name, project, scope, RsSymSymbol::class.java)
            return configs.singleOrNull { it.containingFile.nameWithoutExtension == lookupType.literal }
        }

        val PsiFile.nameWithoutExtension: String
            get() {
                val dot = name.lastIndexOf('.')
                if (dot == -1) {
                    return name
                }
                return name.substring(0, dot)
            }
    }
}
