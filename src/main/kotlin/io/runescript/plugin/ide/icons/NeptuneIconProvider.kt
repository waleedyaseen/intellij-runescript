package io.runescript.plugin.ide.icons

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.neptune.isNeptuneBuildFile
import javax.swing.Icon

class NeptuneIconProvider : FileIconProvider {
    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (file.isNeptuneBuildFile) {
            return RsIcons.Neptune
        }
        return null
    }
}