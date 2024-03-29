package io.runescript.plugin.ide

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object RsIcons {
    val RuneScript: Icon = AllIcons.FileTypes.Java
    val ClientScript: Icon = AllIcons.FileTypes.Java
    val Cs2: Icon = IconLoader.getIcon("/icons/fileTypes/cs2.svg", RsIcons::class.java)
    val Proc: Icon = IconLoader.getIcon("/icons/fileTypes/proc.svg", RsIcons::class.java)

}