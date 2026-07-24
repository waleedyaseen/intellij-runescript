package io.runescript.plugin.lang.psi.typechecker

import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression

class TypeCheckingUtilTest : BasePlatformTestCase() {
    fun testNonPhysicalFileWithoutModuleUsesPsiDependency() {
        val file =
            PsiFileFactory
                .getInstance(project)
                .createFileFromText(
                    "non-physical.cs2",
                    RsFileType,
                    "[proc,test] { return(1); }",
                    0,
                    false,
                )
        val expression = PsiTreeUtil.findChildOfType(file, RsIntegerLiteralExpression::class.java)

        assertNotNull(expression)
        assertEmpty(TypeCheckingUtil.typeCheck(expression!!))
    }
}
