package io.runescript.plugin.ide.doc

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.oplang.psi.RsOpCommand

class RsDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        return getDescription(element)
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return getDescription(element)
    }

    private fun getDescription(element: PsiElement?): String? {
        if (element == null) {
            return null
        }
        when (element) {
            is RsLocalVariableExpression -> {
                val builder = element.project.createDocumentationGenerator()
                val type = when (val parent = element.parent) {
                    is RsParameter -> (parent.typeName ?: parent.arrayTypeLiteral)!!.text
                    is RsLocalVariableDeclarationStatement -> parent.defineType.text.substring(4)
                    is RsArrayVariableDeclarationStatement -> "${parent.defineType.text.substring(4)}array"
                    else -> return null
                }
                if (element.parent is RsParameter) {
                    builder.appendKeyword("parameter ")
                }
                builder.appendHighlighted(element.text, RsSyntaxHighlighterColors.LOCAL_VARIABLE)
                builder.appendColon(": ")
                builder.appendKeyword(type)
                return builder.build()
            }

            is RsScript -> {
                val builder = element.project.createDocumentationGenerator()
                builder.appendHighlighted(element.qualifiedName, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
                builder.appendLParen()
                var appendComma = false
                element.parameterList?.parameterList?.forEach {
                    if (appendComma) {
                        builder.appendComma(", ")
                    } else {
                        appendComma = true
                    }
                    builder.appendKeyword((it.typeName ?: it.arrayTypeLiteral)!!.text)
                    builder.appendHighlighted(" ${it.localVariableExpression.text}", RsSyntaxHighlighterColors.LOCAL_VARIABLE)
                }
                builder.appendRParen()
                appendComma = false
                builder.appendLParen()
                element.returnList?.typeNameList?.forEach {
                    if (appendComma) {
                        builder.appendComma(", ")
                    } else {
                        appendComma = true
                    }
                    builder.appendKeyword(it.text)
                }
                builder.appendRParen()
                return builder.build()
            }

            is RsOpCommand -> {
                val builder = element.project.createDocumentationGenerator()
                builder.appendHighlighted(element.commandHeader.text, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
                builder.appendLParen()
                var appendComma = false
                element.parameterList.parameterList.forEach {
                    if (appendComma) {
                        builder.appendComma(", ")
                    } else {
                        appendComma = true
                    }
                    builder.appendKeyword(it.typeName.text)
                    builder.appendHighlighted(" $${it.nameLiteral.text}", RsSyntaxHighlighterColors.LOCAL_VARIABLE)
                }
                builder.appendRParen()
                appendComma = false
                builder.appendLParen()
                element.returnList.typeNameList.forEach {
                    if (appendComma) {
                        builder.appendComma(", ")
                    } else {
                        appendComma = true
                    }
                    builder.appendKeyword(it.text)
                }
                builder.appendRParen()
                return builder.build()
            }

            else -> return null
        }
    }

    private fun Project.createDocumentationGenerator() = RsDocumentationGenerator(this)
}