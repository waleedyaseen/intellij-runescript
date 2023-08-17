package io.runescript.plugin.lang.psi.type

@Suppress("unused")
enum class RsPrimitiveType(val literal: String, val referencable: Boolean = true) : RsType {
    INT("int"),
    STRING("string"),
    SPOTANIM("spotanim"),
    SEQ("seq"),
    STAT("stat"),
    SYNTH("synth"),
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
    // Non-Referencable types
    PARAM("param", referencable = false),
    FLO("flo", referencable = false),
    FLU("flu", referencable = false),
    VARP("varp", referencable = false),
    VARBIT("varbit", referencable = false),
    VARC("varc", referencable = false),
    NULL("null", referencable = false),
    HOOK("hook", referencable = false),
    VARPHOOK("varphook", referencable = false),
    STATHOOK("stathook", referencable = false),
    INVHOOK("invhook", referencable = false),
    CONSTANT("constant", referencable = false);

    override val representation: String
        get() = literal

    val baseType: RsBaseType
        get() = when(this) {
            STRING -> RsBaseType.STRING
            LONG -> RsBaseType.LONG
            else -> RsBaseType.INT
        }

    companion object {
        private val LOOKUP_REFERENCABLE_BY_LITERAL = RsPrimitiveType.values().associateBy { it.literal }.filterValues { it.referencable }
        private val LOOKUP_BY_LITERAL = RsPrimitiveType.values().associateBy { it.literal }

        fun lookupReferencableOrNull(literal: String) = LOOKUP_REFERENCABLE_BY_LITERAL[literal]
        fun lookupReferencable(literal: String) = lookupReferencableOrNull(literal) ?: error("No primitive type could be found for literal: `$literal`")
        fun lookupOrNull(literal: String) = LOOKUP_BY_LITERAL[literal]
        fun lookup(literal: String) = lookupOrNull(literal) ?: error("No primitive type could be found for literal: `$literal`")
    }
}