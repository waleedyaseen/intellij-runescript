package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.RuneScript

object RsSymElementGenerator {

    fun createField(project: Project, text: String): RsSymField {
        val element = createDummyFile(project, "000\t$text")
        val symField = PsiTreeUtil.findChildOfType(element, RsSymField::class.java)!!.nextSibling.nextSibling
        return symField as RsSymField
    }

    private fun createDummyFile(project: Project, text: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val name = "dummy.sym"
        val virtualFile = LightVirtualFile(name, RsFileType, text)
        return factory.trySetupPsiForFile(virtualFile, RuneScript, false, true)!!
    }
}