package io.runescript.plugin.ide.formatter

import com.intellij.formatting.*
import io.runescript.plugin.ide.formatter.impl.createSpacingBuilder

class RsFormatter : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val element = formattingContext.psiElement
        val styleSettings = formattingContext.codeStyleSettings
        val spaceBuilder = createSpacingBuilder(styleSettings)
        val block = RsFormattingBlock(element.node, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), spaceBuilder)
        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, block, styleSettings)
    }
}