package io.runescript.plugin.symbollang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import io.runescript.plugin.symbollang.RuneScriptSymbol
import io.runescript.plugin.symbollang.filetypes.RsSymFileType

object RsSymElementGenerator {

    fun createField(project: Project, text: String): RsSymField {
        val element = createDummyFile(project, "000\t$text\n")
        val symField = PsiTreeUtil.findChildOfType(element, RsSymField::class.java)!!.nextSibling.nextSibling
        return symField as RsSymField
    }

    private fun createDummyFile(project: Project, text: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val name = "dummy.sym"
        val virtualFile = LightVirtualFile(name, RsSymFileType, StringUtil.convertLineSeparators(text))
        return factory.trySetupPsiForFile(virtualFile, RuneScriptSymbol, false, true)!!
    }
}