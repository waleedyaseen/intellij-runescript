package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableSymbol
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val TYPE_CHECKER_DATA_HOLDER_KEY = Key.create<TypeCheckerDataHolder>("type_checker_data_holder")

var PsiElement.typeCheckerData: TypeCheckerDataHolder?
    get() {
        val parent = checkNotNull(parentOfType<RsInferenceDataHolder>(true))
        return parent.getUserData(TYPE_CHECKER_DATA_HOLDER_KEY)
    }
    set(value) {
        val parent = checkNotNull(parentOfType<RsInferenceDataHolder>(true))
        parent.putUserData(TYPE_CHECKER_DATA_HOLDER_KEY, value)
    }

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation. If the attribute is not found an
 * error is thrown.
 */
fun <T> attribute(key: String): ReadWriteProperty<PsiElement, T> = object : ReadWriteProperty<PsiElement, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PsiElement, property: KProperty<*>): T {
        val value = thisRef.typeCheckerData?.get<T>(thisRef, key)
            ?: throw IllegalStateException("Property '${property.name}' should be initialized before get.")
        return value
    }

    override fun setValue(thisRef: PsiElement, property: KProperty<*>, value: T) {
        checkNotNull(thisRef.typeCheckerData).set(thisRef, key, value)
    }
}

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation, if the attribute is not defined
 * the return value is `null` instead of throwing an error.
 */
fun <T : Any> attributeOrNull(key: String): ReadWriteProperty<PsiElement, T?> =
    object : ReadWriteProperty<PsiElement, T?> {
        override fun getValue(thisRef: PsiElement, property: KProperty<*>): T?
            = thisRef.typeCheckerData?.get(thisRef, key)

        override fun setValue(thisRef: PsiElement, property: KProperty<*>, value: T?) {
            checkNotNull(thisRef.typeCheckerData).set(thisRef, key, value)
        }
    }

/**
 * The scripts defined trigger type.
 */
internal var RsScript.triggerType by attribute<TriggerType>("triggerType")

/**
 * The script parameter type(s) if it returns any.
 */
internal var RsScript.parameterType by attribute<Type>("parameterType")

/**
 * The script return type(s) if it returns any.
 */
internal var RsScript.returnType by attribute<Type>("returnType")

/**
 * The root [SymbolTable] of the script.
 */
internal var RsScript.scope by attribute<LocalVariableTable>("block")

/**
 * The symbol that the parameter declares.
 */
internal var RsParameter.symbol by attribute<LocalVariableSymbol>("symbol")

/**
 * The [LocalVariableTable] of the block.
 */
internal var RsBlockStatement.scope by attribute<LocalVariableTable>("scope")

/**
 * The type the switch statement accepts.
 */
internal var RsSwitchStatement.type by attribute<Type>("type")

/**
 * The [SymbolTable] of the case block.
 */
internal var RsSwitchCase.scope by attribute<LocalVariableTable>("scope")

/**
 * The type that the expression would evaluate to.
 *
 * @see Expression.nullableType
 */
var RsExpression.type: Type by attribute("type")

/**
 * The type that the expression would evaluate to, or `null`.
 *
 * @see Expression.type
 */
var RsExpression.nullableType: Type? by attributeOrNull("type")

/**
 * Allows parents of a node to define the expected type to help with identifier ambiguity.
 */
var RsExpression.typeHint: Type? by attributeOrNull("typeHint")

/**
 * Returns the type of the expression after type checking has been performed.
 */
val RsExpression.typeCheckedType: Type
    get() {
        TypeCheckingUtil.ensureTypeChecked(this)
        return this.nullableType ?: MetaType.Error
    }

/**
 * The scope
 */
var RsStringLiteralExpression.hookScope: LocalVariableTable? by attributeOrNull("hookScope")
