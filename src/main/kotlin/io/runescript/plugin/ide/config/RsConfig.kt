package io.runescript.plugin.ide.config

import io.runescript.plugin.lang.psi.type.RsPrimitiveType

object RsConfig {

    private val PRIMITIVE_TYPES_DEFAULT = listOf(
        "type",
        "param",
        "flo",
        "flu",
        "varp",
        "varbit",
        "varc",
        "varclan",
        "varclansetting",
        "null",
        "int",
        "string",
        "spotanim",
        "seq",
        "stat",
        "synth",
        "midi",
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
        "stylesheet",
        "bugtemplate",
        "clientopnpc",
        "clientoploc",
        "clientopobj",
        "clientopplayer",
        "clientoptile",
        // Types only for the .op file
        "hook",
        "varphook",
        "stathook",
        "invhook",
        "varchook",
        "any",
        "array",
    )

    fun getPrimitiveTypes(): List<String> {
        val arrayTypes = buildList {
            for (type in RsPrimitiveType.entries) {
                add("${type.literal}array")
            }
        }
        return PRIMITIVE_TYPES_DEFAULT + arrayTypes
    }
}