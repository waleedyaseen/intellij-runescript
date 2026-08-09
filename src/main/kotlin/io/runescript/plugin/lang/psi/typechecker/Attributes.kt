package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsBlockStatement
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsParameter
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsStringLiteralExpression
import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableSymbol
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.MetaType
import io.runescript.plugin.lang.psi.typechecker.type.Type
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val TYPE_CHECKER_DATA_HOLDER_KEY = Key.create<TypeCheckerDataHolder>("type_checker_data_holder")

internal var RsInferenceDataHolder.typeCheckerData: TypeCheckerDataHolder?
    get() = getUserData(TYPE_CHECKER_DATA_HOLDER_KEY)
    set(value) = putUserData(TYPE_CHECKER_DATA_HOLDER_KEY, value)

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation. If the attribute is not found an
 * error is thrown.
 */
internal fun <T> attribute(attribute: TypeCheckerAttribute): ReadWriteProperty<PsiElement, T> =
    object : ReadWriteProperty<PsiElement, T> {
        @Suppress("UNCHECKED_CAST")
        override fun getValue(
            thisRef: PsiElement,
            property: KProperty<*>,
        ): T {
            val value =
                TypeCheckingUtil.dataFor(thisRef)?.get<T>(thisRef, attribute)
                    ?: throw IllegalStateException("Property '${property.name}' should be initialized before get.")
            return value
        }

        override fun setValue(
            thisRef: PsiElement,
            property: KProperty<*>,
            value: T,
        ) {
            checkNotNull(TypeCheckingUtil.dataFor(thisRef)).set(thisRef, attribute, value)
        }
    }

/**
 * Returns a [ReadWriteProperty] for accessing attributes through delegation, if the attribute is not defined
 * the return value is `null` instead of throwing an error.
 */
internal fun <T : Any> attributeOrNull(attribute: TypeCheckerAttribute): ReadWriteProperty<PsiElement, T?> =
    object : ReadWriteProperty<PsiElement, T?> {
        override fun getValue(
            thisRef: PsiElement,
            property: KProperty<*>,
        ): T? = TypeCheckingUtil.dataFor(thisRef)?.get(thisRef, attribute)

        override fun setValue(
            thisRef: PsiElement,
            property: KProperty<*>,
            value: T?,
        ) {
            checkNotNull(TypeCheckingUtil.dataFor(thisRef)).set(thisRef, attribute, value)
        }
    }

/**
 * The scripts defined trigger type.
 */
internal var RsScript.triggerType by attribute<TriggerType>(TypeCheckerAttribute.TRIGGER_TYPE)

/**
 * The script parameter type(s) if it returns any.
 */
internal var RsScript.parameterType by attribute<Type>(TypeCheckerAttribute.PARAMETER_TYPE)

/**
 * The script return type(s) if it returns any.
 */
internal var RsScript.returnType by attribute<Type>(TypeCheckerAttribute.RETURN_TYPE)

/**
 * The root [SymbolTable] of the script.
 */
internal var RsScript.scope by attribute<LocalVariableTable>(TypeCheckerAttribute.SCRIPT_SCOPE)

/**
 * The symbol that the parameter declares.
 */
internal var RsParameter.symbol by attribute<LocalVariableSymbol>(TypeCheckerAttribute.PARAMETER_SYMBOL)

/**
 * The [LocalVariableTable] of the block.
 */
internal var RsBlockStatement.scope by attribute<LocalVariableTable>(TypeCheckerAttribute.BLOCK_SCOPE)

/**
 * The type the switch statement accepts.
 */
internal var RsSwitchStatement.type by attribute<Type>(TypeCheckerAttribute.SWITCH_TYPE)

/**
 * The [SymbolTable] of the case block.
 */
internal var RsSwitchCase.scope by attribute<LocalVariableTable>(TypeCheckerAttribute.SWITCH_CASE_SCOPE)

/**
 * The type that the expression would evaluate to.
 *
 * @see Expression.nullableType
 */
var RsExpression.type: Type by attribute(TypeCheckerAttribute.EXPRESSION_TYPE)

/**
 * The type that the expression would evaluate to, or `null`.
 *
 * @see Expression.type
 */
var RsExpression.nullableType: Type? by attributeOrNull(TypeCheckerAttribute.EXPRESSION_TYPE)

/**
 * Allows parents of a node to define the expected type to help with identifier ambiguity.
 */
var RsExpression.typeHint: Type? by attributeOrNull(TypeCheckerAttribute.TYPE_HINT)

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
var RsStringLiteralExpression.hookScope: LocalVariableTable? by attributeOrNull(TypeCheckerAttribute.HOOK_SCOPE)
