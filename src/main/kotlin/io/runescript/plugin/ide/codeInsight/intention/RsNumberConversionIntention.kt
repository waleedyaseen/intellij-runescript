package io.runescript.plugin.ide.codeInsight.intention

import com.intellij.codeInsight.intention.numeric.AbstractNumberConversionIntention
import com.intellij.codeInsight.intention.numeric.NumberConverter
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.elementType
import io.runescript.plugin.lang.psi.RsElementGenerator
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsFile
import java.util.*

class RsNumberConversionIntention : AbstractNumberConversionIntention() {

    override fun extract(element: PsiElement): NumberConversionContext? {
        if (element.elementType == RsElementTypes.INTEGER) {
            var rawValue = element.text
            var negated = false
            if (rawValue[0] == '-' || rawValue[0] == '+') {
                negated = rawValue[0] == '-'
                rawValue = rawValue.substring(1)
            }
            var radix = 10
            if (rawValue.startsWith("0x")) {
                radix = 16
                rawValue = rawValue.substring(2)
            }
            var number = rawValue.toUIntOrNull(radix)?.toInt() ?: return null
            if (negated) number *= -1
            return NumberConversionContext(element, number, element.text, false)
        }
        return null
    }

    override fun getConverters(file: PsiFile): List<NumberConverter> {
        return if (file is RsFile) CONVERTERS else Collections.emptyList()
    }

    override fun replace(sourceElement: PsiElement, replacement: String) {
        sourceElement.replace(RsElementGenerator.createIntegerLiteral(sourceElement.project, replacement))
    }

    companion object {

        private val INTEGER_TO_HEX = object : NumberConverter {
            override fun getConvertedText(text: String, number: Number): String? {
                return if (text.startsWith("0x") || text.startsWith("0X")) {
                    null
                } else {
                    "0x${Integer.toHexString(number.toInt())}"
                }
            }

            override fun toString(): String {
                return "hex"
            }
        }

        private val INTEGER_TO_DECIMAL = object : NumberConverter {
            override fun getConvertedText(text: String, number: Number): String? {
                return if (text.startsWith("0x") || text.startsWith("0X")) {
                    number.toString()
                } else {
                    null
                }
            }

            override fun toString(): String {
                return "decimal"
            }
        }

        private val CONVERTERS = listOf(INTEGER_TO_DECIMAL, INTEGER_TO_HEX)
    }
}