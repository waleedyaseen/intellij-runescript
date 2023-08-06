package io.runescript.plugin.lang.psi

import io.runescript.plugin.lang.psi.type.*

val RsTypeName.type: RsPrimitiveType
    get() = RsPrimitiveType.lookupOrNull(text)!!

val RsReturnList?.type: RsType
    get() {
        if (this == null || typeNameList.isEmpty()) {
            return RsUnitType
        }
        if (typeNameList.size == 1) {
            return typeNameList.first().type
        }
        return RsTupleType(typeNameList.map { it.type }.toTypedArray())
    }