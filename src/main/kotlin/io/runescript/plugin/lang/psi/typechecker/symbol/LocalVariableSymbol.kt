package io.runescript.plugin.lang.psi.typechecker.symbol

import io.runescript.plugin.lang.psi.typechecker.type.Type

data class LocalVariableSymbol(val name: String, val type: Type)
