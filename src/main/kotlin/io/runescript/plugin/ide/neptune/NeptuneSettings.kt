package io.runescript.plugin.ide.neptune

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.externalSystem.settings.AbstractExternalSystemSettings
import com.intellij.openapi.externalSystem.settings.DelegatingExternalSystemSettingsListener
import com.intellij.openapi.externalSystem.settings.ExternalSystemSettingsListener
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.XCollection

@State(name = "NeptuneSettings", storages = [Storage("neptune.xml")])
@Service(Service.Level.PROJECT)
class NeptuneSettings(project: Project) :
    AbstractExternalSystemSettings<NeptuneSettings, NeptuneProjectSettings, NeptuneSettingsListener>(
        NeptuneSettingsListener.TOPIC,
        project
    ), PersistentStateComponent<NeptuneSettingsState> {

    var launcherJre: String = ""
    var neptuneHome: String = ""

    override fun subscribe(
        listener: ExternalSystemSettingsListener<NeptuneProjectSettings>,
        parentDisposable: Disposable
    ) {
        val adapter = object : DelegatingExternalSystemSettingsListener<NeptuneProjectSettings>(listener),
            NeptuneSettingsListener {
            override fun onProjectsUnlinked(linkedProjectPaths: Set<String?>) {
                listener.onProjectsUnlinked(linkedProjectPaths)
            }
        }
        doSubscribe(adapter, parentDisposable)
    }

    override fun copyExtraSettingsFrom(settings: NeptuneSettings) {
        launcherJre = settings.launcherJre
        neptuneHome = settings.neptuneHome
    }

    override fun checkSettings(old: NeptuneProjectSettings, current: NeptuneProjectSettings) {
    }

    override fun getState(): NeptuneSettingsState {
        val state = NeptuneSettingsState()
        fillState(state)
        state.launcherJre = launcherJre
        state.neptuneHome = neptuneHome
        return state
    }

    override fun loadState(state: NeptuneSettingsState) {
        super.loadState(state)
        launcherJre = state.launcherJre
        neptuneHome = state.neptuneHome
    }
}

class NeptuneSettingsState : AbstractExternalSystemSettings.State<NeptuneProjectSettings> {

    private val settings = HashSet<NeptuneProjectSettings>()

    var launcherJre: String = ""
    var neptuneHome: String = ""

    @XCollection(elementTypes = [NeptuneProjectSettings::class])
    override fun getLinkedExternalProjectsSettings(): MutableSet<NeptuneProjectSettings> {
        return settings
    }

    override fun setLinkedExternalProjectsSettings(settings: MutableSet<NeptuneProjectSettings>?) {
        this.settings.addAll(settings ?: return)
    }

}