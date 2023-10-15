package io.runescript.plugin.lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.stubs.StubElement
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.stubs.types.RsFileStubType

class RsFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, RuneScript) {

    override fun getFileType(): FileType {
        return RsFileType
    }

    override fun toString(): String {
        return "RsFile: $name"
    }

}