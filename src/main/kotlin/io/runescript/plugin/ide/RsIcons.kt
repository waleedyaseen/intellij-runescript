package io.runescript.plugin.ide

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object RsIcons {

    val Neptune: Icon = IconLoader.getIcon("/icons/neptune.svg", RsIcons::class.java)
    val RuneScript: Icon = IconLoader.getIcon("/icons/runescript.svg", RsIcons::class.java)
    val Cs2FileType: Icon = IconLoader.getIcon("/icons/fileTypes/cs2.svg", RsIcons::class.java)
    val SymFileType: Icon = IconLoader.getIcon("/icons/fileTypes/sym.svg", RsIcons::class.java)
    val Cs2: Icon = AllIcons.Nodes.AbstractMethod
    val Proc: Icon = AllIcons.Nodes.Method

}