package io.runescript.plugin.ide.codeInsight

import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandlerWithTabActionSupport
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.startOffset
import io.runescript.plugin.lang.psi.*

class RsGosubParameterInfoProvider : ParameterInfoHandlerWithTabActionSupport<RsArgumentList, CallInfo, RsExpression> {

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): RsArgumentList? {
        val file = context.file as? RsFile ?: return null
        val element = file.findElementAt(context.offset) ?: return null
        val argumentList = element.parentOfType<RsArgumentList>() ?: return null
        val gosub = argumentList.parentOfType<RsGosubExpression>() ?: return null
        val reference = gosub.reference?.resolve() ?: return null
        reference as RsScript
        val parameters = reference.parameterList?.parameterList ?: emptyList()
        context.itemsToShow = arrayOf(CallInfo.of(parameters))
        return argumentList
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): RsArgumentList? {
        val element = context.file.findElementAt(context.offset) ?: return null
        val argumentList = element.parentOfType<RsArgumentList>() ?: return null
        val arguments = getActualParameters(argumentList)
        val index = findParameterIndex(argumentList, context.offset)
        context.setCurrentParameter(index)
        if (index >= 0 && index < arguments.size) {
            val argument = arguments[index]
            context.highlightedParameter = argument
        } else {
            context.highlightedParameter = null
        }
        return argumentList
    }

    private fun findParameterIndex(argumentList: RsArgumentList, offset: Int): Int {
        var index = 0
        var node = argumentList.node.firstChildNode
        while (node != null) {
            if (node.elementType == RsElementTypes.COMMA && node.startOffset < offset) {
                index++
            }
            node = node.treeNext
        }
        return index
    }

    override fun updateUI(p: CallInfo, context: ParameterInfoUIContext) {
        if (context.parameterOwner == null || !context.parameterOwner.isValid) {
            context.isUIComponentEnabled = false
            return
        }
        var highlightStartOffset = -1
        var highlightEndOffset = -1
        val text = buildString {
            for ((index, parameter) in p.parameters.withIndex()) {
                if (index != 0) {
                    append(", ")
                }
                if (index == context.currentParameterIndex) {
                    highlightStartOffset = length
                }
                renderParameter(parameter)
                if (index == context.currentParameterIndex) {
                    highlightEndOffset = length
                }
            }
        }
        context.setupUIComponentPresentation(
            text,
            highlightStartOffset,
            highlightEndOffset,
            false,
            false,
            false,
            context.defaultParameterColor
        )
    }

    private fun StringBuilder.renderParameter(parameter: CallParameterInfo) {
        append(parameter.typeName)
        append(": ")
        append(parameter.parameterName)
    }

    override fun updateParameterInfo(parameterOwner: RsArgumentList, context: UpdateParameterInfoContext) {
        if (context.parameterOwner !== parameterOwner) {
            context.removeHint()
        }
        val parameterIndex = findParameterIndex(parameterOwner, context.offset)
        context.setCurrentParameter(parameterIndex)
    }

    override fun showParameterInfo(element: RsArgumentList, context: CreateParameterInfoContext) {
        context.showHint(element, element.startOffset, this)
    }

    override fun getActualParameterDelimiterType(): IElementType {
        return RsElementTypes.COMMA
    }

    override fun getActualParametersRBraceType(): IElementType {
        return RsElementTypes.RPAREN
    }

    override fun getArgumentListAllowedParentClasses(): Set<Class<*>> {
        return setOf(RsGosubExpression::class.java)
    }

    override fun getArgListStopSearchClasses(): Set<Class<*>> {
        return setOf(RsArgumentList::class.java)
    }

    override fun getArgumentListClass(): Class<RsArgumentList> {
        return RsArgumentList::class.java
    }

    override fun getActualParameters(o: RsArgumentList): Array<RsExpression> {
        return o.expressionList.toTypedArray()
    }
}

data class CallInfo(
    val parameters: List<CallParameterInfo>
) {
    companion object {

        fun of(parameters: List<RsParameter>) = CallInfo(parameters.map {
            val typeName = it.arrayTypeLiteral?.text ?: it.typeName?.text ?: "unknown"
            val parameterName = it.localVariableExpression?.name ?: return@map CallParameterInfo(typeName, "<unknown-parameter>")
            CallParameterInfo(typeName, parameterName)
        })
    }
}

data class CallParameterInfo(
    val typeName: String,
    val parameterName: String
)