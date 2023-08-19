package io.runescript.plugin.ide.formatter.style

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

@Suppress("PropertyName", "unused")
class RsCodeStyleSettings(container: CodeStyleSettings) : CustomCodeStyleSettings("RuneScriptCodeStyleSettings", container) {

    @JvmField
    var SPACE_WITHIN_ARRAY_BOUNDS = false

    @JvmField
    var SPACE_BEFORE_ARRAY_BOUNDS = false

    @JvmField
    var SPACE_BEFORE_CALC_PARENTHESES = false

    @JvmField
    var SPACE_WITHIN_CALC_PARENTHESES = false

    @JvmField
    var SPACE_WITHIN_RETURN_LIST_PARENTHESES = false

    @JvmField
    var SPACE_BEFORE_COMMA_IN_RETURN_LIST = false

    @JvmField
    var SPACE_AFTER_COMMA_IN_RETURN_LIST = false
}