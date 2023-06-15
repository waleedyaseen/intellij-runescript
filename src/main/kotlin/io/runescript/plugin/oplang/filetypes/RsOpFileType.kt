package io.runescript.plugin.oplang.filetypes

import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.lang.RuneScript

object RsOpFileType : LanguageFileType(RuneScript) {

    override fun getDisplayName() = "RuneScript Op"

    override fun getName() = "RuneScript Op"

    override fun getDescription() = "Command source file"

    override fun getDefaultExtension() = "op"

    override fun getIcon() = RsIcons.ClientScript
}