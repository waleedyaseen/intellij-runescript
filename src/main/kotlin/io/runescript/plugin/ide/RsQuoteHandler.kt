package io.runescript.plugin.ide

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import io.runescript.plugin.lang.psi.RuneScriptTypes

class RsQuoteHandler : SimpleTokenSetQuoteHandler(RuneScriptTypes.STRING_START, RuneScriptTypes.STRING_PART, RuneScriptTypes.STRING_TAG, RuneScriptTypes.STRING_END)