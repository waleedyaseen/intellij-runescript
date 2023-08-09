package io.runescript.plugin.lang.psi.type.inference

import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.RsVisitor
import io.runescript.plugin.lang.psi.type.RsType

data class RsTypeDiagnostic(val element: PsiElement, val message: String)

class RsTypeInference(private val context: RsInferenceDataHolder) : RsVisitor() {

    private val types = mutableMapOf<PsiElement, RsType>()
    private val typeHints = mutableMapOf<PsiElement, RsType>()
    private val errors = mutableListOf<RsTypeDiagnostic>()

    fun infer() {
        val visitor = RsTypeInferenceVisitor(this)
        context.accept(visitor)
    }

    fun typeOf(element: PsiElement): RsType? {
        return types[element]
    }

    fun typeHintOf(element: PsiElement): RsType? {
        return typeHints[element]
    }

    fun typeInferred(element: PsiElement, type: RsType?) {
        if (type == null) {
            types -= element
        } else {
            types[element] = type
        }
    }

    fun typeHintInferred(element: PsiElement, type: RsType?)  {
        if (type == null) {
            typeHints -= element
        } else {
            typeHints[element] = type
        }
    }

    fun addError(element: PsiElement, message: String) {
        errors.add(RsTypeDiagnostic(element, message))
    }

    fun errorsFor(element: PsiElement): List<RsTypeDiagnostic>? {
        return errors.filter { it.element === element }
    }
}