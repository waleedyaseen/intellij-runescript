package io.runescript.plugin.symbollang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.symbollang.RuneScriptSymbol
import io.runescript.plugin.symbollang.filetypes.RsSymFileType

class RsSymFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScriptSymbol) {

    override fun getFileType(): FileType {
        return RsSymFileType
    }

    override fun toString(): String {
        return "RuneScript Symbol File"
    }
}