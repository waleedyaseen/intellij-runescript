package io.runescript.plugin.ide.neptune

import com.intellij.openapi.components.*
import com.intellij.openapi.components.serviceOrNull
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.typechecker.command.DynamicCommandHandler
import io.runescript.plugin.lang.psi.typechecker.command.impl.*
import io.runescript.plugin.lang.psi.typechecker.command.impl.array.*
import io.runescript.plugin.lang.psi.typechecker.command.impl.debug.DumpCommandHandler
import io.runescript.plugin.lang.psi.typechecker.command.impl.debug.ScriptCommandHandler
import io.runescript.plugin.lang.psi.typechecker.trigger.ClientTriggerType
import io.runescript.plugin.lang.psi.typechecker.trigger.CommandTrigger
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerManager
import io.runescript.plugin.lang.psi.typechecker.type.*
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.*

@State(
    name = "NeptuneModuleData",
    storages = [Storage(StoragePathMacros.MODULE_FILE)]
)
class NeptuneModuleData : SerializablePersistentStateComponent<NeptuneModuleData.State>(State()) {

    data class State(
        var dbFindReturnsCount: Boolean = false,
        var ccCreateAssertNewArg: Boolean = false,
        var prefixPostfixExpressions: Boolean = false,
        var arraysV2: Boolean = false,
        var simplifiedTypeCodes: Boolean = false,
    )

    val dbFindReturnsCount: Boolean
        get() = state.dbFindReturnsCount
    val ccCreateAssertNewArg: Boolean
        get() = state.ccCreateAssertNewArg
    val prefixPostfixExpressions: Boolean
        get() = state.prefixPostfixExpressions
    val arraysV2: Boolean
        get() = state.arraysV2
    val simplifiedTypeCodes: Boolean
        get() = state.simplifiedTypeCodes

    /**
     * The [TypeManager] for the compiler that is used for registering and looking up types.
     */
    var types: TypeManager = TypeManager()

    /**
     * The [TriggerManager] for the compiler that is used for registering and looking up script triggers.
     */
    var triggers: TriggerManager = TriggerManager()

    /**
     * A mapping of command names to their handler implementation.
     */
    val dynamicCommandHandlers = mutableMapOf<String, DynamicCommandHandler>()

    var symbolLoaders = mutableMapOf<String, (subTypes: Type) -> Type>()

    override fun loadState(state: State) {
        super.loadState(state)
        registerDefaultTypesAndTriggers()
    }

    fun updateFromImportData(importData: NeptuneProjectImportData) {
        updateState {
            it.dbFindReturnsCount = importData.dbFindReturnsCount
            it.ccCreateAssertNewArg = importData.ccCreateAssertNewArg
            it.prefixPostfixExpressions = importData.prefixPostfixExpressions
            it.arraysV2 = importData.arraysV2
            it.simplifiedTypeCodes = importData.simplifiedTypeCodes
            it
        }
        registerDefaultTypesAndTriggers()
    }

    private fun registerDefaultTypesAndTriggers() {
        types = TypeManager()
        triggers = TriggerManager()
        dynamicCommandHandlers.clear()
        symbolLoaders.clear()

        triggers.registerAll<ClientTriggerType>()

        // register the core types
        types.registerAll<PrimitiveType>()
        types.register(MetaType.Any)
        types.register("type", MetaType.Type(MetaType.Any))
        if (arraysV2) {
            types.register("array", ArrayType(MetaType.Any))
        }
        setupDefaultTypeCheckers()

        // register the command trigger
        triggers.register(CommandTrigger)

        // register types
        types.registerAll<ScriptVarType>()
        types.register("param", ParamCommandHandler.PARAM_ANY)
        types.changeOptions("long") {
            allowDeclaration = false
        }

        // special types for commands
        types.register("hook", MetaType.Hook(MetaType.Unit))
        types.register("stathook", MetaType.Hook(ScriptVarType.STAT))
        types.register("invhook", MetaType.Hook(ScriptVarType.INV))
        types.register("varphook", MetaType.Hook(VarPlayerType(MetaType.Any)))
        types.register("dbcolumn", DbColumnType(MetaType.Any))
        types.register("ifscript", IfScriptType(MetaType.Any))
        types.register(
            "gclientclicknpc",
            MetaType.Script(ClientTriggerType.GCLIENTCLICKNPC, MetaType.Unit, MetaType.Unit),
        )
        types.register(
            "gclientclickloc",
            MetaType.Script(ClientTriggerType.GCLIENTCLICKLOC, MetaType.Unit, MetaType.Unit),
        )
        types.register(
            "gclientclickobj",
            MetaType.Script(ClientTriggerType.GCLIENTCLICKOBJ, MetaType.Unit, MetaType.Unit),
        )
        types.register(
            "gclientclickplayer",
            MetaType.Script(ClientTriggerType.GCLIENTCLICKPLAYER, MetaType.Unit, MetaType.Unit),
        )
        types.register(
            "gclientclicktile",
            MetaType.Script(ClientTriggerType.GCLIENTCLICKTILE, MetaType.Unit, MetaType.Unit),
        )

        // allow assignment of namedobj to obj
        types.addTypeChecker { left, right -> left == ScriptVarType.OBJ && right == ScriptVarType.NAMEDOBJ }

        // treat varp as alias of varp<int>
        types.addTypeChecker { left, right ->
            (left is VarPlayerType && left.inner == PrimitiveType.INT && right == ScriptVarType.VARP) ||
                    (left == ScriptVarType.VARP && right is VarPlayerType && right.inner == PrimitiveType.INT)
        }

        // register the dynamic command handlers
        if (ccCreateAssertNewArg) {
            addDynamicCommandHandler("cc_create", CcCreateCommandHandler(), dot = true)
        }
        addDynamicCommandHandler("enum", EnumCommandHandler())
        addDynamicCommandHandler("oc_param", ParamCommandHandler(ScriptVarType.OBJ))
        addDynamicCommandHandler("nc_param", ParamCommandHandler(ScriptVarType.NPC))
        addDynamicCommandHandler("lc_param", ParamCommandHandler(ScriptVarType.LOC))
        addDynamicCommandHandler("struct_param", ParamCommandHandler(ScriptVarType.STRUCT))
        addDynamicCommandHandler("if_param", IfParamCommandHandler(cc = false))
        addDynamicCommandHandler("cc_param", IfParamCommandHandler(cc = true), dot = true)
        addDynamicCommandHandler("if_setparam", IfSetParamCommandHandler(cc = false))
        addDynamicCommandHandler("cc_setparam", IfSetParamCommandHandler(cc = true), dot = true)

        addDynamicCommandHandler("if_runscript*", IfRunScriptCommandHandler())
        addDynamicCommandHandler("if_find_child", IfFindChildCommandHandler(), dot = true)
        addDynamicCommandHandler("if_children_filter", IfChildrenFilterCommandHandler())

        if (dbFindReturnsCount) {
            addDynamicCommandHandler("db_find", DbFindCommandHandler(true))
            addDynamicCommandHandler("db_find_refine", DbFindCommandHandler(true))
        } else {
            addDynamicCommandHandler("db_find", DbFindCommandHandler(false))
            addDynamicCommandHandler("db_find_with_count", DbFindCommandHandler(true))
            addDynamicCommandHandler("db_find_refine", DbFindCommandHandler(false))
            addDynamicCommandHandler("db_find_refine_with_count", DbFindCommandHandler(true))
        }
        addDynamicCommandHandler("db_getfield", DbGetFieldCommandHandler())

        if (arraysV2) {
            addDynamicCommandHandler("array_compare", ArrayCompareCommandHandler())
            addDynamicCommandHandler("array_indexof", ArraySearchCommandHandler())
            addDynamicCommandHandler("array_lastindexof", ArraySearchCommandHandler())
            addDynamicCommandHandler("array_count", ArraySearchCommandHandler())
            addDynamicCommandHandler("array_min", ArrayMinMaxCommandHandler())
            addDynamicCommandHandler("array_max", ArrayMinMaxCommandHandler())
            addDynamicCommandHandler("array_fill", ArrayFillCommandHandler())
            addDynamicCommandHandler("array_copy", ArrayCopyCommandHandler())
            addDynamicCommandHandler("array_create", ArrayCreateCommandHandler())
            addDynamicCommandHandler("array_push", ArrayPushCommandHandler())
            addDynamicCommandHandler("array_insert", ArrayInsertCommandHandler())
            addDynamicCommandHandler("array_delete", ArrayDeleteCommandHandler())
            addDynamicCommandHandler("array_pushall", ArrayPushallCommandHandler())
            addDynamicCommandHandler("array_insertall", ArrayInsertallCommandHandler())
            addDynamicCommandHandler("enum_getinputs", EnumGetInputsOutputsCommandHandler())
            addDynamicCommandHandler("enum_getoutputs", EnumGetInputsOutputsCommandHandler())
        }

        addDynamicCommandHandler("event_opbase", PlaceholderCommand(PrimitiveType.STRING, "event_opbase"))
        addDynamicCommandHandler("event_mousex", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 1))
        addDynamicCommandHandler("event_mousey", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 2))
        addDynamicCommandHandler("event_com", PlaceholderCommand(ScriptVarType.COMPONENT, Int.MIN_VALUE + 3))
        addDynamicCommandHandler("event_op", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 4))
        addDynamicCommandHandler("event_comsubid", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 5))
        addDynamicCommandHandler("event_com2", PlaceholderCommand(ScriptVarType.COMPONENT, Int.MIN_VALUE + 6))
        addDynamicCommandHandler("event_comsubid2", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 7))
        addDynamicCommandHandler("event_keycode", PlaceholderCommand(PrimitiveType.INT, Int.MIN_VALUE + 8))
        addDynamicCommandHandler("event_keychar", PlaceholderCommand(PrimitiveType.CHAR, Int.MIN_VALUE + 9))

        addDynamicCommandHandler("dump", DumpCommandHandler())
        addDynamicCommandHandler("script", ScriptCommandHandler())

        // symbol loaders
//        addSymConstantLoaders()
//
        addSymLoader("ai_queue", ScriptVarType.AI_QUEUE)
        addSymLoader("area", ScriptVarType.AREA)
        addSymLoader("bas", ScriptVarType.BAS)
        addSymLoader("bugtemplate", ScriptVarType.BUG_TEMPLATE)
        addSymLoader("category", ScriptVarType.CATEGORY)
        addSymLoader("chatcat", ScriptVarType.CHATCAT)
        addSymLoader("chatphrase", ScriptVarType.CHATPHRASE)
        addSymLoader("clientinterface", ScriptVarType.CLIENTINTERFACE)
        addSymLoader("component", ScriptVarType.COMPONENT)
        addSymLoader("controller", ScriptVarType.CONTROLLER)
        addSymLoader("cursor", ScriptVarType.CURSOR)
        addSymLoader("cutscene", ScriptVarType.CUTSCENE)
        addSymLoader("dbcolumn") { DbColumnType(it) }
        addSymLoader("dbrow", ScriptVarType.DBROW)
        addSymLoader("dbtable", ScriptVarType.DBTABLE)
        addSymLoader("enum", ScriptVarType.ENUM)
        addSymLoader("fontmetrics", ScriptVarType.FONTMETRICS)
        addSymLoader("graphic", ScriptVarType.GRAPHIC)
        addSymLoader("headbar", ScriptVarType.HEADBAR)
        addSymLoader("hitmark", ScriptVarType.HITMARK)
        addSymLoader("hunt", ScriptVarType.HUNT)
        addSymLoader("idkit", ScriptVarType.IDKIT)
        addSymLoader("if_script") { IfScriptType(it) }
        addSymLoader("interface", ScriptVarType.INTERFACE)
        addSymLoader("inv", ScriptVarType.INV)
        addSymLoader("jingle", ScriptVarType.JINGLE)
        addSymLoader("loc", ScriptVarType.LOC)
        addSymLoader("locshape", ScriptVarType.LOC_SHAPE)
        addSymLoader("mapelement", ScriptVarType.MAPELEMENT)
        addSymLoader("mapsceneicon", ScriptVarType.MAPSCENEICON)
        addSymLoader("material", ScriptVarType.MATERIAL)
        addSymLoader("midi", ScriptVarType.MIDI)
        addSymLoader("model", ScriptVarType.MODEL)
        addSymLoader("movespeed", ScriptVarType.MOVESPEED)
        addSymLoader("npc", ScriptVarType.NPC)
        addSymLoader("npc_mode", ScriptVarType.NPC_MODE)
        addSymLoader("npc_stat", ScriptVarType.NPC_STAT)
        addSymLoader("obj", ScriptVarType.NAMEDOBJ)
        addSymLoader("overlayinterface", ScriptVarType.OVERLAYINTERFACE)
        addSymLoader("param") { ParamType(it) }
        addSymLoader("quest", ScriptVarType.QUEST)
        addSymLoader("seq", ScriptVarType.SEQ)
        addSymLoader("gamelogevent", ScriptVarType.GAMELOGEVENT)
        addSymLoader("audiogroup", ScriptVarType.AUDIOGROUP)
        addSymLoader("skybox", ScriptVarType.SKYBOX)
        addSymLoader("skydecor", ScriptVarType.SKYDECOR)
        addSymLoader("social_network", ScriptVarType.SOCIAL_NETWORK)
        addSymLoader("spotanim", ScriptVarType.SPOTANIM)
        addSymLoader("stat", ScriptVarType.STAT)
        addSymLoader("stringvector", ScriptVarType.STRINGVECTOR)
        addSymLoader("struct", ScriptVarType.STRUCT)
        addSymLoader("synth", ScriptVarType.SYNTH)
        addSymLoader("texture", ScriptVarType.TEXTURE)
        addSymLoader("toplevelinterface", ScriptVarType.TOPLEVELINTERFACE)
        addSymLoader("varbit", VarBitType)
        addSymLoader("varc") { VarClientType(it) }
        addSymLoader("varclan") { VarClanType(it) }
        addSymLoader("varclansetting") { VarClanSettingsType(it) }
        addSymLoader("varcstr") { VarClientType(PrimitiveType.STRING) }
        addSymLoader("varp") { VarPlayerType(it) }
        addSymLoader("vorbis", ScriptVarType.VORBIS)
        addSymLoader("wma", ScriptVarType.MAPAREA)
        addSymLoader("writeinv", ScriptVarType.WRITEINV)
    }

    private fun setupDefaultTypeCheckers() {
        // allow anything to be assigned to any (top type)
        types.addTypeChecker { left, _ -> left == MetaType.Any }

        // allow nothing to be assigned to any (bottom type)
        types.addTypeChecker { _, right -> right == MetaType.Nothing }

        // allow anything to be assigned to error to prevent error propagation
        types.addTypeChecker { left, right -> left == MetaType.Error || right == MetaType.Error }

        // basic checker where both types are equal
        types.addTypeChecker { left, right -> left == right }

        // checker for Script types that compares parameter and return types
        types.addTypeChecker { left, right ->
            left is MetaType.Script &&
                    right is MetaType.Script &&
                    left.trigger == right.trigger &&
                    types.check(left.parameterType, right.parameterType) &&
                    types.check(left.returnType, right.returnType)
        }

        // checker for Hook types that compares the trigger list type.
        types.addTypeChecker { left, right ->
            left is MetaType.Hook &&
                    right is MetaType.Hook &&
                    types.check(left.transmitListType, right.transmitListType)
        }

        // checker for ArrayType which requires exact match on inner types, with a special case to
        // allow anyarray <- typearray
        types.addTypeChecker { left, right ->
            left is ArrayType &&
                    right is ArrayType &&
                    (left.inner == right.inner || left.inner == MetaType.Any)
        }

        // checker for WrappedType that compares the inner types
        types.addTypeChecker { left, right ->
            left is WrappedType &&
                    right is WrappedType &&
                    left::class == right::class &&
                    types.check(left.inner, right.inner)
        }
    }

    /**
     * Adds a [DynamicCommandHandler] to the compiler with the given [name]. See
     * [DynamicCommandHandler] for information on implementation. If [dot], then
     * the "dot" version of the command is also registered by prepending a
     * period to name.
     *
     * If a handler was registered for the [name] already an error is thrown.
     */
    fun addDynamicCommandHandler(name: String, handler: DynamicCommandHandler, dot: Boolean = false) {
        val existing = dynamicCommandHandlers.putIfAbsent(name, handler)
        if (existing != null) {
            error("A dynamic command handler with the name of '$name' already exists.")
        }

        if (dot) {
            addDynamicCommandHandler(".$name", handler, dot = false)
        }
    }

    /**
     * Helper for loading external symbols from `sym` files with a specific [type].
     */
    private fun addSymLoader(name: String, type: Type) {
        addSymLoader(name) { type }
    }

    /**
     * Helper for loading external symbols from `sym` files with subtypes.
     */
    private fun addSymLoader(name: String, typeSuppler: (subTypes: Type) -> Type) {
        check(name !in symbolLoaders)
        symbolLoaders[name] = typeSuppler
    }
}

val DEFAULT_TYPE_MANAGER = TypeManager()

val Module.neptuneModuleData: NeptuneModuleData
    get() = service<NeptuneModuleData>()

val PsiElement.neptuneModuleData: NeptuneModuleData?
    get() = ModuleUtil.findModuleForFile(containingFile)?.neptuneModuleData

val PsiElement.typeManagerOrDefault: TypeManager
    get() = neptuneModuleData?.types ?: DEFAULT_TYPE_MANAGER

val Project?.typeManagerOrDefault: TypeManager
    get() = this?.modules?.firstNotNullOfOrNull {
        it.serviceOrNull<NeptuneModuleData>()
    }?.types ?: DEFAULT_TYPE_MANAGER
