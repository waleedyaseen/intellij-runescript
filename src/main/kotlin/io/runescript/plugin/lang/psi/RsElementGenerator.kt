package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import io.runescript.plugin.ide.filetypes.Cs2FileType
import io.runescript.plugin.lang.RuneScript


object RsElementGenerator {

    fun createNameLiteral(project: Project, text: String): RsNameLiteral {
        val element = createDummyFile(project, "[$text,dummy]")
        return PsiTreeUtil.findChildOfType(element, RsNameLiteral::class.java) as RsNameLiteral
    }

    private fun createDummyFile(project: Project, text: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val name = "dummy.${Cs2FileType.defaultExtension}"
        val virtualFile = LightVirtualFile(name, Cs2FileType, text)
        return factory.trySetupPsiForFile(virtualFile, RuneScript, false, true)!!
    }
}