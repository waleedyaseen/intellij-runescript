package io.runescript.plugin.ide.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

object RsConfig {

    private const val PRIMITIVE_TYPES_KEY = "runescript.settings.primitivetypes"
    private val PRIMITIVE_TYPES_DEFAULT = listOf(
            "int",
            "string",
            "long"
    )

    fun getPrimitiveTypes(project: Project?): List<String> {
        return if (project == null) {
            PropertiesComponent.getInstance().getList(PRIMITIVE_TYPES_KEY)
        } else {
            PropertiesComponent.getInstance(project).getList(PRIMITIVE_TYPES_KEY)
        } ?: PRIMITIVE_TYPES_DEFAULT
    }

    fun setPrimitiveTypes(project: Project, types: List<String>?) {
        PropertiesComponent.getInstance(project).setList(PRIMITIVE_TYPES_KEY, types)
    }
}