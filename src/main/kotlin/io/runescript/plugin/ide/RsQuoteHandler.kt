package io.runescript.plugin.ide

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import io.runescript.plugin.lang.psi.RsElementTypes

class RsQuoteHandler : SimpleTokenSetQuoteHandler(RsElementTypes.STRING_START, RsElementTypes.STRING_PART, RsElementTypes.STRING_TAG, RsElementTypes.STRING_END)