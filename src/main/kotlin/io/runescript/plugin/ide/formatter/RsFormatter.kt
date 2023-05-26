package io.runescript.plugin.ide.formatter

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import io.runescript.plugin.ide.formatter.blocks.impl.RsFileBlock
import io.runescript.plugin.ide.formatter.impl.RsSpacingBuilder
import io.runescript.plugin.ide.formatter.style.RsCodeStyleSettings
import io.runescript.plugin.lang.RuneScript

class RsFormatter : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val element = formattingContext.psiElement
        val styleSettings = formattingContext.codeStyleSettings
        val commonSettings = styleSettings.getCommonSettings(RuneScript)
        val customSettings = styleSettings.getCustomSettings(RsCodeStyleSettings::class.java)
        val spacingBuilder = RsSpacingBuilder(commonSettings, customSettings)
        val context = RsFormatterContext(spacingBuilder)
        val block = RsFileBlock(context, element.node)
        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, block, styleSettings)
    }
}