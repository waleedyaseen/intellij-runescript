package io.runescript.plugin.ide

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import io.runescript.plugin.lang.psi.RsTypes

class RsQuoteHandler : SimpleTokenSetQuoteHandler(RsTypes.STRING_START, RsTypes.STRING_PART, RsTypes.STRING_TAG, RsTypes.STRING_END)