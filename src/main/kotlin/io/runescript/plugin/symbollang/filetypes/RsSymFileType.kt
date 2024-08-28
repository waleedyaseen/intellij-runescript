package io.runescript.plugin.symbollang.filetypes

import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.symbollang.RuneScriptSymbol


object RsSymFileType : LanguageFileType(RuneScriptSymbol) {

    override fun getDisplayName() = "RuneScript Symbol"

    override fun getName() = "RuneScript Symbol"

    override fun getDescription() = "RuneScript symbol file"

    override fun getDefaultExtension() = "sym"

    override fun getIcon() = RsIcons.SymFileType
}