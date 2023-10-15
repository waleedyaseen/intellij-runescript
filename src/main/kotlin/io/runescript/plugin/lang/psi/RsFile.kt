package io.runescript.plugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.RuneScript

class RsFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScript) {

    override fun getFileType(): FileType {
        return RsFileType
    }

    override fun toString(): String {
        return "RsFile: $name"
    }

}