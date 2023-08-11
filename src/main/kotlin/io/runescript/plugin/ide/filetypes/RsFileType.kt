package io.runescript.plugin.ide.filetypes

import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.lang.RuneScript

object RsFileType : LanguageFileType(RuneScript) {

    override fun getDisplayName() = "RuneScript"

    override fun getName() = "RuneScript"

    override fun getDescription() = "RuneScript source file"

    override fun getDefaultExtension() = "cs2"

    override fun getIcon() = RsIcons.ClientScript
}