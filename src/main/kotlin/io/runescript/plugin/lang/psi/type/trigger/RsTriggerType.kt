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
        arrayOf(INT, INT),
    ),
    WORLDMAPELEMENTMOUSELEAVE(
        "worldmapelementmouseleave",
        subjectType = MAPELEMENT,
        allowParameters = true,
        arrayOf(INT, INT),
    ),
    WORLDMAPELEMENTMOUSEREPEAT(
        "worldmapelementmouserepeat",
        subjectType = MAPELEMENT,
        allowParameters = true,
        arrayOf(INT, INT),
    ),
    CLIENTOPNPC("clientopnpc"),
    CLIENTOPLOC("clientoploc"),
    CLIENTOPOBJ("clientopobj"),
    CLIENTOPPLAYER("clientopplayer"),
    CLIENTOPTILE("clientoptile"),
    LOADNPC("loadnpc", subjectType = NPC),
    UNLOADNPC("unloadnpc", subjectType = NPC),
    LOADLOC("loadloc", subjectType = LOC),
    UNLOADLOC("unloadloc", subjectType = LOC),
    LOADOBJ("loadobj", subjectType = NAMEDOBJ),
    UNLOADOBJ("unloadobj", subjectType = NAMEDOBJ),
    LOADPLAYER("loadplayer"),
    UNLOADPLAYER("unloadplayer"),
    UPDATEOBJSTACK("updateobjstack"),
    UPDATEOBJCOUNT("updateobjcount", subjectType = NAMEDOBJ),
    PLAYER_DESTINATION("player_destination"),
    PLAYER_HOVER("player_hover"),
    PLAYER_MOVE("player_move"),
    PROC("proc", allowParameters = true, allowReturns = true),
    CLIENTSCRIPT("clientscript", allowParameters = true),
    ONCLICKLOC("onclickloc"),
    ONCLICKOBJ("onclickobj"),
    ONCLICKNPC("onclicknpc"),
    ONCLICKPLAYER("onclickplayer"),
    MINIMENU_OPENED("minimenu_opened"),
    ;

    companion object {
        private val LOOKUP_BY_LITERAL = values().associateBy { it.literal }
        fun lookup(literal: String) = LOOKUP_BY_LITERAL[literal]
    }
}