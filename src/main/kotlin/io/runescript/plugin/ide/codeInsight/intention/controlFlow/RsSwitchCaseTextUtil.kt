package io.runescript.plugin.ide.codeInsight.intention.controlFlow

import io.runescript.plugin.lang.psi.RsSwitchCase
import io.runescript.plugin.lang.psi.RsSwitchStatement

internal fun RsSwitchStatement.textWithCases(orderedCases: List<RsSwitchCase>): String? {
    val cases = switchCaseList
    if (cases.isEmpty() || orderedCases.size != cases.size || orderedCases.toSet() != cases.toSet()) return null
    val lbrace = lbrace ?: return null
    val rbrace = rbrace ?: return null
    val switchOffset = textOffset
    val blocks =
        cases
            .mapIndexed { index, case ->
                val start = if (index == 0) lbrace.textRange.endOffset else cases[index - 1].textRange.endOffset
                case to text.substring(start - switchOffset, case.textRange.endOffset - switchOffset)
            }.toMap()
    val prefix = text.substring(0, lbrace.textRange.endOffset - switchOffset)
    val suffix = text.substring(cases.last().textRange.endOffset - switchOffset, text.length)
    return prefix + orderedCases.joinToString("") { blocks.getValue(it) } + suffix
}
