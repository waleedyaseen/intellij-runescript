package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.TupleType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import io.runescript.plugin.lang.psi.typechecker.type.TypeManager


fun rawSymToType(
    symbol: RsSymSymbol,
    typeManager: TypeManager,
    loaders: Map<String, (subTypes: Type) -> Type>
): Type {
    val typeName = resolveToSymTypeName(symbol.containingFile)
    val typeSupplier = loaders[typeName] ?: return MetaType.Error
    val subTypes = if (symbol.fieldList.size >= 3 && symbol.fieldList[2].text.isNotBlank()) {
        val typeSplit = symbol.fieldList[2].text.split(',')
        val types = typeSplit.map { typeName -> typeManager.find(typeName, allowArray = true) }
        TupleType.fromList(types)
    } else {
        MetaType.Unit
    }
    return typeSupplier(subTypes)
}

fun resolveToSymTypeName(file: PsiFile?): String? {
    if (file == null) return null
    val module = ModuleUtilCore.findModuleForPsiElement(file)
    val moduleDir = module?.guessModuleDir() ?: return null
    val neptuneModuleData = module.neptuneModuleData
    for (symbolPath in neptuneModuleData.symbolPaths) {
        val symbolsDir = moduleDir.findFileByRelativePath(symbolPath) ?: continue
        val typeName = resolveToSymTypeName(symbolsDir, file.virtualFile)
        if (typeName != null) {
            return typeName
        }
    }
    return null
}

fun resolveToSymTypeName(symbolsRoot: VirtualFile, file: VirtualFile): String? {
    val dir1 = file.parent
    if (dir1 == symbolsRoot) {
        return file.nameWithoutExtension
    }
    val dir2 = dir1.parent
    if (symbolsRoot == dir2) {
        return dir1.name
    }
    return null
}

fun PsiFile.isConstantFile() = resolveToSymTypeName(this) == "constant"

fun PsiFile.isVarFile() = when (resolveToSymTypeName(this)) {
    "varbit", "varc", "varclan", "varclansetting", "varcstr", "varp" -> true
    else -> false
}
