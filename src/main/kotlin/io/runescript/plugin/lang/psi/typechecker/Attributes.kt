package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableSymbol
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation. If the attribute is not found an
 * error is thrown.
 */
fun <T> attribute(key: String): ReadWriteProperty<PsiElement, T> =
    attribute(Key.create<T>(key))

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation. If the attribute is not found an
 * error is thrown.
 */
fun <T> attribute(key: Key<T>): ReadWriteProperty<PsiElement, T> = object : ReadWriteProperty<PsiElement, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: PsiElement, property: KProperty<*>): T {
        val value = thisRef.getUserData(key)
            ?: throw IllegalStateException("Property '${property.name}' should be initialized before get.")
        return value
    }

    override fun setValue(thisRef: PsiElement, property: KProperty<*>, value: T) {
        thisRef.putUserData(key, value)
    }
}

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation, if the attribute is not defined
 * the return value is `null` instead of throwing an error.
 */
fun <T : Any> attributeOrNull(key: String): ReadWriteProperty<PsiElement, T?> =
    attributeOrNull(Key.create<T>(key))

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation, if the attribute is not defined
 * the return value is `null` instead of throwing an error.
 */
fun <T : Any> attributeOrNull(key: Key<T>): ReadWriteProperty<PsiElement, T?> =
    object : ReadWriteProperty<PsiElement, T?> {
        override fun getValue(thisRef: PsiElement, property: KProperty<*>): T? = thisRef.getUserData(key)

        override fun setValue(thisRef: PsiElement, property: KProperty<*>, value: T?) {
            thisRef.putUserData(key, value)
        }
    }

private val TYPE_KEY = Key.create<Type>("type")

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
var RsExpression.type: Type by attribute(TYPE_KEY)

/**
 * The type that the expression would evaluate to, or `null`.
 *
 * @see Expression.type
 */
var RsExpression.nullableType: Type? by attributeOrNull(TYPE_KEY)

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
