package io.runescript.plugin.lang.psi

fun RsStringLiteralContent.isBasicContent(): Boolean {
    val first = node.firstChildNode ?: return true
    return first == node.lastChildNode && first.elementType == RsElementTypes.STRING_PART
}