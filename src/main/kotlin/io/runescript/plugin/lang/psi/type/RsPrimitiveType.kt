package io.runescript.plugin.lang.psi.type

@Suppress("unused")
enum class RsPrimitiveType(val literal: String, val referencable: Boolean = true) : RsType {
    INT("int"),
    STRING("string"),
    SPOTANIM("spotanim"),
    SEQ("seq"),
    STAT("stat"),
    SYNTH("synth"),
    MIDI("midi"),
    COORD("coord"),
    CHAR("char"),
    FONTMETRICS("fontmetrics"),
    WMA("wma"),
    ENUM("enum"),
    NPC("npc"),
    MODEL("model"),
    TOPLEVELINTERFACE("toplevelinterface"),
    OVERLAYINTERFACE("overlayinterface"),
    CLIENTINTERFACE("clientinterface"),
    INTERFACE("interface"),
    COMPONENT("component"),
    LONG("long"),
    BOOLEAN("boolean"),
    CATEGORY("category"),
    NAMEDOBJ("namedobj"),
    OBJ("obj"),
    INV("inv"),
    TEXTURE("texture"),
    MAPELEMENT("mapelement"),
    GRAPHIC("graphic"),
    STRUCT("struct"),
    LOC("loc"),
    COLOUR("colour"),
    IDKIT("idkit"),
    CHATPHRASE("chatphrase"),
    BAS("bas"),
    DBROW("dbrow"),
    ENTITYOVERLAY("entityoverlay"),
    NPC_UID("npc_uid"),
    LOCSHAPE("locshape"),
    DBTABLE("dbtable"),
    DBCOLUMN("dbcolumn"),
    PLAYER_UID("player_uid"),
    STRINGVECTOR("stringvector"),
    STYLESHEET("stylesheet"),
    BUGTEMPLATE("bugtemplate"),
    CLIENTOPNPC("clientopnpc"),
    CLIENTOPLOC("clientoploc"),
    CLIENTOPOBJ("clientopobj"),
    CLIENTOPPLAYER("clientopplayer"),
    CLIENTOPTILE("clientoptile"),
    VARP("varp"),
    // Non-Referencable types
    ANY("any", referencable = false),
    ARRAY("array", referencable = false),
    TYPE("type", referencable = false),
    PARAM("param", referencable = false),
    FLO("flo", referencable = false),
    FLU("flu", referencable = false),
    VARBIT("varbit", referencable = false),
    VARCLAN("varclan", referencable = false),
    VARCLANSETTING("varclansetting", referencable = false),
    VARC("varc", referencable = false),
    NULL("null", referencable = false),
    HOOK("hook", referencable = false),
    VARPHOOK("varphook", referencable = false),
    VARCHOOK("varchook", referencable = false),
    STATHOOK("stathook", referencable = false),
    INVHOOK("invhook", referencable = false),
    CONSTANT("constant", referencable = false),
    CLIENTSCRIPT("clientscript", referencable = false);

    override val representation: String
        get() = literal

    val baseType: RsBaseType
        get() = when (this) {
            STRING -> RsBaseType.STRING
            LONG -> RsBaseType.LONG
            else -> RsBaseType.INT
        }

    val isDeclarable: Boolean
        get() = referencable

    companion object {

        private val LOOKUP_REFERENCABLE_BY_LITERAL = RsPrimitiveType.values()
            .associateBy { it.literal }
            .filterValues { it.referencable }

        private val LOOKUP_BY_LITERAL = RsPrimitiveType.values()
            .associateBy { it.literal }

        fun lookupReferencableOrNull(literal: String) = LOOKUP_REFERENCABLE_BY_LITERAL[literal]

        fun lookupReferencable(literal: String) = lookupReferencableOrNull(literal)
            ?: error("No primitive type could be found for literal: `$literal`")

        fun lookupOrNull(literal: String) = LOOKUP_BY_LITERAL[literal]

        fun lookup(literal: String) = lookupOrNull(literal)
            ?: error("No primitive type could be found for literal: `$literal`")
    }
}