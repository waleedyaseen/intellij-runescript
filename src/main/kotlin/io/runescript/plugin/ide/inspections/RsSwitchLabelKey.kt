package io.runescript.plugin.ide.inspections

import io.runescript.plugin.lang.psi.RsBooleanLiteralExpression
import io.runescript.plugin.lang.psi.RsCoordLiteralExpression
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsIntegerLiteralExpression
import io.runescript.plugin.lang.psi.RsLongLiteralExpression
import io.runescript.plugin.lang.psi.RsNullLiteralExpression
import io.runescript.plugin.lang.psi.RsStringLiteralExpression

internal fun RsExpression.switchLabelKey(): Any? =
    when (this) {
        is RsIntegerLiteralExpression -> integerKey(text, false)
        is RsLongLiteralExpression -> integerKey(text.removeSuffix("L").removeSuffix("l"), true)
        is RsBooleanLiteralExpression -> "boolean:${text.lowercase()}"
        is RsCoordLiteralExpression -> "coord:$text"
        is RsNullLiteralExpression -> "null"
        is RsStringLiteralExpression -> "string:$text"
        else -> null
    }

private fun integerKey(
    text: String,
    long: Boolean,
): String {
    val negative = text.startsWith('-')
    var digits = text.removePrefix("-").removePrefix("+")
    val radix = if (digits.startsWith("0x", ignoreCase = true)) 16 else 10
    if (radix == 16) digits = digits.substring(2)
    val value =
        if (long) {
            val parsed = digits.toULongOrNull(radix)?.toLong()
            parsed?.let { if (negative) -it else it }
        } else {
            val parsed = digits.toUIntOrNull(radix)?.toInt()
            parsed?.let { if (negative) -it else it }
        }
    return "${if (long) "long" else "int"}:${value ?: text.lowercase()}"
}
