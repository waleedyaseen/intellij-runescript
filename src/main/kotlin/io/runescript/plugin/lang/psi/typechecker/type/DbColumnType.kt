package io.runescript.plugin.lang.psi.typechecker.type

import com.google.common.base.MoreObjects
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.WrappedType

/**
 * Represents a database column. The [inner] type is what is returned when accessing
 * the data of a row.
 */
class DbColumnType(override val inner: Type) : WrappedType {
    override val representation: String = "dbcolumn<${inner.representation}>"
    override val code: Char? = null
    override val baseType: BaseVarType = BaseVarType.INTEGER
    override val defaultValue: Any = -1
    override val options: TypeOptions = MutableTypeOptions(
        allowSwitch = false,
        allowArray = false,
        allowDeclaration = false,
    )

    override fun toString(): String = MoreObjects.toStringHelper(this)
        .add("inner", inner)
        .toString()
}
