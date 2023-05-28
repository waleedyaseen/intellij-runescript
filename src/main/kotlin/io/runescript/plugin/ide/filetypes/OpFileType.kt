package io.runescript.plugin.ide.filetypes

import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.lang.RuneScript

object OpFileType : LanguageFileType(RuneScript) {

    override fun getDisplayName() = "Command"

    override fun getName() = "Command"

    override fun getDescription() = "Command source file"

    override fun getDefaultExtension() = "op"

    override fun getIcon() = RsIcons.ClientScript
}