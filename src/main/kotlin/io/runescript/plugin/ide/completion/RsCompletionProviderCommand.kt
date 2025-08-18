package io.runescript.plugin.ide.completion

import io.runescript.plugin.ide.completion.insertHandler.RsScriptInsertHandler
import io.runescript.plugin.lang.stubs.index.RsCommandScriptIndex

class RsCompletionProviderCommand() :
    RsCompletionProviderScriptBase(RsCommandScriptIndex.KEY, RsScriptInsertHandler(null))
