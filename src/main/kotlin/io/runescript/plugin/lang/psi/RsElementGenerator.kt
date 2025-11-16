package io.runescript.plugin.lang.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.LightVirtualFile
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.doc.findDescendantOfType
import io.runescript.plugin.lang.doc.psi.impl.RsDocLink
import io.runescript.plugin.symbollang.filetypes.RsSymFileType
import io.runescript.plugin.symbollang.psi.RsSymSymbol

object RsElementGenerator {

    fun createLocalVariableDeclaration(
        project: Project,
        type: String,
        name: String,
        initializer: String
    ): RsLocalVariableDeclarationStatement {
        val element = createDummyFile(project, "[proc,dummy]()()def_$type $$name = $initializer;")
        return PsiTreeUtil.findChildOfType(
            element,
            RsLocalVariableDeclarationStatement::class.java
        ) as RsLocalVariableDeclarationStatement
    }

    fun createLocalVariableExpression(project: Project, name: String): RsLocalVariableExpression {
        val element = createDummyFile(project, "[proc,dummy]()()$$name;")
        return PsiTreeUtil.findChildOfType(element, RsLocalVariableExpression::class.java) as RsLocalVariableExpression
    }

    fun createConditionExpression(project: Project, expression: String): RsExpression {
        val element = createDummyFile(project, "[proc,dummy]()()if ($expression){}")
        return PsiTreeUtil.findChildOfType(element, RsExpression::class.java) as RsExpression

    }

    fun createIntegerLiteral(project: Project, replacement: String): PsiElement {
        val element = createDummyFile(project, "[proc,dummy]()()$replacement;")
        val literal =
            PsiTreeUtil.findChildOfType(element, RsIntegerLiteralExpression::class.java) as RsIntegerLiteralExpression
        return literal.node.firstChildNode.psi!!
    }

    fun createColorTag(project: Project, color: Int, tagName: String = "col"): PsiElement {
        val element = createDummyFile(project, "[proc,dummy]()()\"<$tagName=%06x>\";".format(color and 0xffffff))
        val literal =
            PsiTreeUtil.findChildOfType(element, RsStringLiteralExpression::class.java) as RsStringLiteralExpression
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

    fun createExpression(project: Project, text: String): RsExpression {
        val element = createDummyFile(project, "[proc,dummy]()()$text;")
        return PsiTreeUtil.findChildOfType(element, RsExpressionStatement::class.java)?.expression as RsExpression
    }

    fun createStringLiteralContent(project: Project, content: String): RsStringLiteralContent {
        val element = createDummyFile(project, "[proc,dummy]()()\"$content\";")
        val literal =
            PsiTreeUtil.findChildOfType(element, RsStringLiteralExpression::class.java) as RsStringLiteralExpression
        return PsiTreeUtil.findChildOfType(literal, RsStringLiteralContent::class.java) as RsStringLiteralContent
    }

    fun createSymSymbol(project: Project, content: String): RsSymSymbol {
        val element = createDummySymFile(project, content)
        val symbol = PsiTreeUtil.findChildOfType(element, RsSymSymbol::class.java) as RsSymSymbol
        return symbol
    }

    fun createDocIdentifier(project: Project, name: String): PsiElement {
        val element = createDummyFile(project, "/** [$name] */[proc,dummy]()()")
        return element.findDescendantOfType<RsDocLink>()!!.firstChild.nextSibling
    }

    fun createExpressionStatement(project: Project, text: String): RsExpressionStatement {
        val element = createDummyFile(project, "[proc,dummy]()()$text;")
        return PsiTreeUtil.findChildOfType(element, RsExpressionStatement::class.java) as RsExpressionStatement
    }

    private fun createDummyFile(project: Project, text: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val name = "dummy.${RsFileType.defaultExtension}"
        val virtualFile = LightVirtualFile(name, RsFileType, StringUtil.convertLineSeparators(text))
        return factory.trySetupPsiForFile(virtualFile, RuneScript, false, true)!!
    }

    private fun createDummySymFile(project: Project, text: String): PsiFile {
        val factory = PsiFileFactory.getInstance(project) as PsiFileFactoryImpl
        val name = "dummy.${RsSymFileType.defaultExtension}"
        val virtualFile = LightVirtualFile(name, RsSymFileType, StringUtil.convertLineSeparators(text))
        return factory.trySetupPsiForFile(virtualFile, RuneScript, false, true)!!
    }

    fun createNewLine(project: Project): PsiElement {
        return createDummyFile(project, "\n").firstChild
    }
}