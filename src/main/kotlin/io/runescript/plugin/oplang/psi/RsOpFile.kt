package io.runescript.plugin.oplang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.oplang.filetypes.RsOpFileType
import io.runescript.plugin.oplang.psi.stub.type.RsOpFileStubType

class RsOpFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScript) {

    init {
        init(RsOpFileStubType, RsOpFileStubType)
    }

    override fun getFileType(): FileType {
        return RsOpFileType
    }

    override fun toString(): String {
        return "RuneScript Command File"
    }
}