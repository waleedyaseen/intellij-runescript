package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import io.runescript.plugin.ide.filetypes.Cs2FileType
import io.runescript.plugin.lang.RuneScript

object RsElementGenerator {

    fun createColorTag(project: Project, color: Int, tagName: String = "col"): PsiElement {
        val element = createDummyFile(project, "[proc,dummy]()()\"<$tagName=%06x>\";".format(color and 0xffffff))
        val literal = PsiTreeUtil.findChildOfType(element, RsStringLiteralExpression::class.java) as RsStringLiteralExpression
        return literal.node.findChildByType(RsElementTypes.STRING_TAG)!!.psi
    }

    fun createScript(project: Project, text: String): RsScript {
        val element = createDummyFile(project, text)
        return PsiTreeUtil.findChildOfType(element, RsScript::class.java) as RsScript
    }

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