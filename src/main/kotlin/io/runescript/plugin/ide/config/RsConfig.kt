package io.runescript.plugin.ide.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

object RsConfig {

    private const val PRIMITIVE_TYPES_KEY = "runescript.settings.primitivetypes"
    private val PRIMITIVE_TYPES_DEFAULT = listOf(
        "type",
        "param",
        "flo",
        "flu",
        "varp",
        "varbit",
        "varc",
        "null",
        "int",
        "string",
        "spotanim",
        "seq",
        "stat",
        "synth",
        "coord",
        "char",
        "fontmetrics",
        "wma",
        "enum",
        "npc",
        "model",
        "toplevelinterface",
        "overlayinterface",
        "clientinterface",
        "interface",
        "component",
        "long",
        "boolean",
        "category",
        "namedobj",
        "obj",
        "inv",
        "texture",
        "mapelement",
        "graphic",
        "struct",
        "loc",
        "colour",
        "idkit",
        "chatphrase",
        "bas",
        "dbrow",
        "entityoverlay",
        "npc_uid",
        "locshape",
        "dbtable",
        "dbcolumn",
        "player_uid",
        "stringvector",
        // Types only for the .op file
        "hook",
        "varphook",
        "stathook",
        "invhook",
        "any"
    )

    fun getPrimitiveTypes(project: Project?): List<String> {
        return PRIMITIVE_TYPES_DEFAULT
    }

    fun setPrimitiveTypes(project: Project, types: List<String>?) {
        PropertiesComponent.getInstance(project).setList(PRIMITIVE_TYPES_KEY, types)
    }
}