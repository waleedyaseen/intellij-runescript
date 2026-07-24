package io.runescript.plugin.ide.execution.build

import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.task.ModuleBuildTask
import com.intellij.task.ProjectTaskContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.ide.neptune.NeptuneSettings
import java.util.concurrent.TimeUnit

class RsBuildTaskRunnerTest : BasePlatformTestCase() {
    fun testMissingJdkCompletesCoroutineBuildAsFailure() {
        project.service<NeptuneSettings>().launcherJre = "missing-test-jdk"

        val result =
            checkNotNull(
                RsBuildTaskRunner()
                    .run(project, ProjectTaskContext(), moduleBuildTask())
                    .blockingGet(10, TimeUnit.SECONDS),
            )

        assertFalse(result.isAborted)
        assertTrue(result.hasErrors())
    }

    private fun moduleBuildTask(): ModuleBuildTask =
        object : ModuleBuildTask {
            override fun getModule(): Module = module

            override fun isIncrementalBuild(): Boolean = true

            override fun isIncludeDependentModules(): Boolean = false

            override fun isIncludeRuntimeDependencies(): Boolean = false

            override fun getPresentableName(): String = "Build RuneScript module"
        }
}
