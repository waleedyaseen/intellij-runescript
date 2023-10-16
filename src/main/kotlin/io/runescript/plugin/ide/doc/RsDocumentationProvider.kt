package io.runescript.plugin.ide.doc

import com.intellij.codeInsight.javadoc.JavaDocExternalFilter
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup.DEFINITION_END
import com.intellij.lang.documentation.DocumentationMarkup.DEFINITION_START
import com.intellij.lang.documentation.DocumentationSettings
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.richcopy.HtmlSyntaxInfoUtil
import com.intellij.psi.*
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.doc.RsDocRenderer.renderRsDoc
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.*
import java.util.function.Consumer

class RsDocumentationProvider : AbstractDocumentationProvider() {

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        return getDescription(element)
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        return getDescription(element)
    }

    override fun generateHoverDoc(element: PsiElement, originalElement: PsiElement?): String? {
        return getDescription(element)
    }

    private fun getDescription(element: PsiElement?): String? {
        when (element) {
            is RsLocalVariableExpression -> {
                val builder = StringBuilder()
                val parameterDoc = findParamDoc(element)
                if (!parameterDoc.isNullOrBlank()) {
                    builder.append(DEFINITION_START)
                }
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
                if (!parameterDoc.isNullOrBlank()) {
                    builder.append(DEFINITION_END)
                    builder.append(parameterDoc)
                }
                return builder.toString()
            }


            is RsScript -> {
                val builder = StringBuilder()

                builder.append(DEFINITION_START)
                builder.appendScriptDefinition(element)
                builder.append(DEFINITION_END)

                element.findDoc()?.let { builder.renderRsDoc(it.getDefaultSection()) }

                return builder.toString()
            }

            else -> return null
        }
    }

    private fun findParamDoc(variable: RsLocalVariableExpression): String? {
        if (variable.parent !is RsParameter) {
            return null
        }
        val script = variable.parentOfType<RsScript>() ?: return null
        val doc = script.findDoc() ?: return null
        val sections = doc.getAllSections()
        for (section in sections) {
            val paramTags = section.findTagsByName("param")
            if (paramTags.isEmpty()) {
                continue
            }
            val paramTag = paramTags.firstOrNull { it.getSubjectName() == variable.nameLiteral.text }
            if (paramTag != null) {
                return buildString { renderRsDoc(paramTag) }
            }
        }
        return null
    }

    private fun StringBuilder.appendScriptDefinition(script: RsScript) {
        appendHighlighted(script.qualifiedName, RsSyntaxHighlighterColors.SCRIPT_DECLARATION)
        appendLParen()
        var appendComma = false
        script.parameterList?.parameterList?.forEach {
            if (appendComma) {
                appendComma(", ")
            } else {
                appendComma = true
            }
            appendKeyword((it.typeName ?: it.arrayTypeLiteral)!!.text)
            appendHighlighted(
                " ${it.localVariableExpression.text}",
                RsSyntaxHighlighterColors.LOCAL_VARIABLE
            )
        }
        appendRParen()
        appendComma = false
        appendLParen()
        script.returnList?.typeNameList?.forEach {
            if (appendComma) {
                appendComma(", ")
            } else {
                appendComma = true
            }
            appendKeyword(it.text)
        }
        appendRParen()
    }

    override fun collectDocComments(file: PsiFile, sink: Consumer<in PsiDocCommentBase>) {
        if (file !is RsFile) return
        SyntaxTraverser.psiTraverser(file)
            .filterIsInstance<RsDoc>()
            .forEach { sink.accept(it) }
    }

    override fun generateRenderedDoc(comment: PsiDocCommentBase): String? {
        val docComment = comment as? RsDoc ?: return null
        val result = StringBuilder().also {
            it.renderRsDoc(docComment.getDefaultSection(), docComment.getAllSections())
        }
        return JavaDocExternalFilter.filterInternalDocInfo(result.toString())
    }

    override fun getDocumentationElementForLink(
        psiManager: PsiManager,
        link: String,
        context: PsiElement
    ): PsiElement? {
        val parts = link.split("/")
        if (parts.size < 2) {
            return null
        }
        val typeName = parts[0]
        val elementName = parts[1]
        if (typeName == "parameter") {
            // TODO(Walied): Find a better way to do this
            val doc = context.containingFile.findElementAt(parts[2].toInt())?.parentOfType<RsDoc>() ?: return null
            val owner = doc.owner
            return RsDocReference.resolve(context.project, owner, null, elementName).singleOrNull()
        } else {
            return RsDocReference.resolve(context.project, null, typeName, elementName).singleOrNull()
        }
    }
}

private fun StringBuilder.appendLParen() {
    appendHighlighted("(", RsSyntaxHighlighterColors.PARENTHESIS)
}

private fun StringBuilder.appendRParen() {
    appendHighlighted(")", RsSyntaxHighlighterColors.PARENTHESIS)
}

private fun StringBuilder.appendComma(text: String = ",") {
    appendHighlighted(text, RsSyntaxHighlighterColors.COMMA)
}

private fun StringBuilder.appendKeyword(keyword: String) {
    appendHighlighted(keyword, RsSyntaxHighlighterColors.KEYWORD)
}

private fun StringBuilder.appendHighlighted(keyword: String, attributes: TextAttributesKey) {
    @Suppress("UnstableApiUsage")
    HtmlSyntaxInfoUtil.appendStyledSpan(
        this,
        attributes,
        keyword,
        DocumentationSettings.getHighlightingSaturation(false)
    )
}

private fun StringBuilder.appendColon(text: String = ":") {
    appendHighlighted(text, RsSyntaxHighlighterColors.COLON)
}

fun RsScript.findDoc(): RsDoc? {
    return childrenOfType<RsDoc>().firstOrNull()
}