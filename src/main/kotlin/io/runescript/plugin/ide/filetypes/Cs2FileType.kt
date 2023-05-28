package io.runescript.plugin.ide.filetypes

import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.lang.RuneScript

object Cs2FileType : LanguageFileType(RuneScript) {

    override fun getDisplayName() = "ClientScript"

    override fun getName() = "ClientScript"

    override fun getDescription() = "ClientScript2 source file"

    override fun getDefaultExtension() = "cs2"

    override fun getIcon() = RsIcons.ClientScript
}