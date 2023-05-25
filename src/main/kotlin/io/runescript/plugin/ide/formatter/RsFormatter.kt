package io.runescript.plugin.ide.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import io.runescript.plugin.ide.formatter.blocks.impl.RsFileBlock
import io.runescript.plugin.ide.formatter.impl.createSpacingBuilder

class RsFormatter : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val element = formattingContext.psiElement
        val styleSettings = formattingContext.codeStyleSettings
        val spacingBuilder = createSpacingBuilder(styleSettings)
        val context = RsFormatterContext(spacingBuilder)
        val block = RsFileBlock(context, element.node)
        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, block, styleSettings)
    }

    override fun getRangeAffectingIndent(file: PsiFile?, offset: Int, elementAtOffset: ASTNode?): TextRange? {
        return super.getRangeAffectingIndent(file, offset, elementAtOffset)
    }
}