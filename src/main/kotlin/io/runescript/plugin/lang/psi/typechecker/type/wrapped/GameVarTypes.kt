package io.runescript.plugin.lang.psi.typechecker.type.wrapped

import io.runescript.plugin.lang.psi.typechecker.type.BaseVarType
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import io.runescript.plugin.lang.psi.typechecker.type.Type


// base game variable type
sealed interface GameVarType : WrappedType {
    override val code: Char?
        get() = null

    override val baseType: BaseVarType?
        get() = null

    override val defaultValue: Any?
        get() = null
}

// implementations
data class VarPlayerType(override val inner: Type) : GameVarType {
    override val representation: String = "varp<${inner.representation}>"
}

data object VarBitType : GameVarType {
    override val inner: Type = PrimitiveType.INT
    override val representation: String = "varbit<${inner.representation}>"
}

data class VarClientType(override val inner: Type) : GameVarType {
    override val representation: String = "varc<${inner.representation}>"
}

data class VarClanType(override val inner: Type) : GameVarType {
    override val representation: String = "varclan<${inner.representation}>"
}

data class VarClanSettingsType(override val inner: Type) : GameVarType {
    override val representation: String = "varclansettings<${inner.representation}>"
}
