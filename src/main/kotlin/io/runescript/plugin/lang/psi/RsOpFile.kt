package io.runescript.plugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.ide.filetypes.OpFileType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.stubs.types.RsOpFileStubType

class RsOpFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScript) {

    init {
        init(RsOpFileStubType, RsOpFileStubType)
    }

    override fun getFileType(): FileType {
        return OpFileType
    }

    override fun toString(): String {
        return "RuneScript Command File"
    }
}