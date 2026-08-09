package io.runescript.plugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.ide.filetypes.RsFileType

class RsFile(
    viewProvider: FileViewProvider,
) : PsiFileBase(viewProvider, viewProvider.baseLanguage) {
    override fun getFileType(): FileType = RsFileType

    override fun toString(): String = "RsFile: $name"
}
