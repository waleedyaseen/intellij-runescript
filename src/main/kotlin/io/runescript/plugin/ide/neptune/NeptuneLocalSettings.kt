package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemLocalSettings
import com.intellij.openapi.project.Project
import java.io.Serializable

@State(name = "NeptuneLocalSettings", storages = [Storage(StoragePathMacros.CACHE_FILE)])
@Service(Service.Level.PROJECT)
class NeptuneLocalSettings(
    project: Project,
) : AbstractExternalSystemLocalSettings<NeptuneLocalSettingsState>(Neptune.SYSTEM_ID, project, NeptuneLocalSettingsState()),
    PersistentStateComponent<NeptuneLocalSettingsState> {
    override fun loadState(state: NeptuneLocalSettingsState) {
    }
}

class NeptuneLocalSettingsState :
    AbstractExternalSystemLocalSettings.State(),
    Serializable
