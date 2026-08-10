package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.fileTypes.FileType
import com.intellij.refactoring.RefactoringCodeVisionSupport
import io.runescript.plugin.ide.filetypes.RsFileType

class RsRefactoringCodeVisionSupport : RefactoringCodeVisionSupport() {
    override fun supportsRename(fileType: FileType): Boolean = fileType === RsFileType

    override fun supportsChangeSignature(fileType: FileType): Boolean = fileType === RsFileType
}
