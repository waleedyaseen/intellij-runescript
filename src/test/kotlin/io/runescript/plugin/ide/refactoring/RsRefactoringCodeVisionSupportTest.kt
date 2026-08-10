package io.runescript.plugin.ide.refactoring

import com.intellij.refactoring.RefactoringCodeVisionSupport
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.symbollang.filetypes.RsSymFileType

class RsRefactoringCodeVisionSupportTest : BasePlatformTestCase() {
    fun testEnablesRenameAndChangeSignatureCodeVisionForRuneScript() {
        assertTrue(RefactoringCodeVisionSupport.isRenameCodeVisionEnabled(RsFileType))
        assertTrue(RefactoringCodeVisionSupport.isChangeSignatureCodeVisionEnabled(RsFileType))
    }

    fun testDoesNotClaimSymbolFilesWithoutChangeSignatureSupport() {
        val support = RsRefactoringCodeVisionSupport()

        assertFalse(support.supportsRename(RsSymFileType))
        assertFalse(support.supportsChangeSignature(RsSymFileType))
    }

    fun testRegistersExactlyOneRuneScriptRefactoringCodeVisionSupport() {
        val supports = RefactoringCodeVisionSupport.EP_NAME.extensionList.filterIsInstance<RsRefactoringCodeVisionSupport>()

        assertEquals(1, supports.size)
    }
}
