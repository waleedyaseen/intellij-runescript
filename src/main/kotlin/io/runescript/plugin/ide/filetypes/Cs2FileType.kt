package io.runescript.plugin.ide.filetypes

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import io.runescript.plugin.lang.RuneScript
import javax.swing.Icon

class Cs2FileType : LanguageFileType(RuneScript) {

    override fun getName() = "ClientScript"

    override fun getDescription() = "ClientScript2 source file"

    override fun getDefaultExtension() = "cs2"

    override fun getIcon(): Icon? = AllIcons.FileTypes.Xml
}