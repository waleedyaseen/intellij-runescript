package io.runescript.plugin.lang.psi.type.trigger

import io.runescript.plugin.lang.psi.type.RsPrimitiveType
import io.runescript.plugin.lang.psi.type.RsPrimitiveType.*

enum class RsTriggerType(
    val literal: String,
    val subjectType: RsPrimitiveType? = null,
    val allowParameters: Boolean = false,
    val parameterTypes: Array<RsPrimitiveType>? = null,
    val allowReturns: Boolean = false
) {
    COMMAND("command", allowParameters = true, allowReturns = true),
    OPWORLDMAPELEMENT1("opworldmapelement1", subjectType = MAPELEMENT),
    OPWORLDMAPELEMENT2("opworldmapelement2", subjectType = MAPELEMENT),
    OPWORLDMAPELEMENT3("opworldmapelement3", subjectType = MAPELEMENT),
    OPWORLDMAPELEMENT4("opworldmapelement4", subjectType = MAPELEMENT),
    OPWORLDMAPELEMENT5("opworldmapelement5", subjectType = MAPELEMENT),
    WORLDMAPELEMENTMOUSEOVER(
        "worldmapelementmouseover",
        subjectType = MAPELEMENT,
        allowParameters = true,
        arrayOf(INT, INT)
    ),
    WORLDMAPELEMENTMOUSELEAVE(
        "worldmapelementmouseleave",
        subjectType = MAPELEMENT,
        allowParameters = true,
        arrayOf(INT, INT)
    ),
    WORLDMAPELEMENTMOUSEREPEAT(
        "worldmapelementmouserepeat",
        subjectType = MAPELEMENT,
        allowParameters = true,
        arrayOf(INT, INT)
    ),
    LOADNPC("loadnpc", subjectType = NPC),
    LOADLOC("loadloc", subjectType = LOC),
    UPDATEOBJSTACK("updateobjstack"),
    UPDATEOBJCOUNT("updateobjcount", subjectType = NAMEDOBJ),
    TRIGGER_47("trigger_47"),
    TRIGGER_48("trigger_48"),
    TRIGGER_49("trigger_49"),
    CLIENTSCRIPT("clientscript", allowParameters = true),
    PROC("proc", allowParameters = true, allowReturns = true),
    ONCLICKLOC("onclickloc"),
    ONCLICKOBJ("onclickobj"),
    ONCLICKNPC("onclicknpc"),
    ONCLICKPLAYER("onclickplayer"),
    TRIGGER82("trigger_82"),
    SHIFTOPNPC("shiftopnpc"),
    SHIFTOPLOC("shiftoploc"),
    SHIFTOPOBJ("shiftopobj"),
    SHIFTOPPLAYER("shiftopplayer"),
    SHIFTOPTILE("shiftoptile"),
    ;

    companion object {
        private val LOOKUP_BY_LITERAL = values().associateBy { it.literal }
        fun lookup(literal: String) = LOOKUP_BY_LITERAL[literal]
    }
}