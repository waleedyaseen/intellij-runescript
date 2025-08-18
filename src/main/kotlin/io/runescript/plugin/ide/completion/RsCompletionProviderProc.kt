package io.runescript.plugin.ide.completion

import io.runescript.plugin.ide.completion.insertHandler.RsScriptInsertHandler
import io.runescript.plugin.lang.stubs.index.RsProcScriptIndex

class RsCompletionProviderProc() :
    RsCompletionProviderScriptBase(RsProcScriptIndex.KEY, RsScriptInsertHandler("~"))
