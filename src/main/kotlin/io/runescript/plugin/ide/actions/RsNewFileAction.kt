package io.runescript.plugin.ide.actions

import com.intellij.ide.actions.CreateFileAction
import com.intellij.openapi.project.DumbAware
import io.runescript.plugin.ide.RsIcons

class RsNewFileAction :
    CreateFileAction("RuneScript File", "Create RuneScript source file", RsIcons.FileTypes.ClientScript), DumbAware {

    override fun getDefaultExtension() = "cs2"

    override fun startInWriteAction() = false
}