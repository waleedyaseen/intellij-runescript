package io.runescript.plugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.ide.filetypes.Cs2FileType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.stubs.types.RsFileStubType

class RsFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScript) {

    init {
        init(RsFileStubType, RsFileStubType)
    }

    override fun getFileType(): FileType {
        return Cs2FileType
    }

    override fun toString(): String {
        return "RuneScript File"
    }
}