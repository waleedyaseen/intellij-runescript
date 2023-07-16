package io.runescript.plugin.lang.psi.type

@Suppress("unused")
enum class RsPrimitiveType(val literal: String) : RsType {
    PARAM("param"),
    FLO("flo"),
    FLU("flu"),
    VARP("varp"),
    VARBIT("varbit"),
    VARC("varc"),
    NULL("null"),
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
    STRINGVECTOR("stringvector");

    override val representation: String
        get() = literal

    val baseType: RsBaseType
        get() = when(this) {
            STRING -> RsBaseType.STRING
            LONG -> RsBaseType.LONG
            else -> RsBaseType.INT
        }

    companion object {
        private val LOOKUP_BY_LITERAL = RsPrimitiveType.values().associateBy { it.literal }
        fun lookupOrNull(literal: String) = LOOKUP_BY_LITERAL[literal]
        fun lookup(literal: String) = lookupOrNull(literal) ?: error("No primitive type could be found for literal: `$literal`")
    }
}