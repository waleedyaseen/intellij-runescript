package io.runescript.plugin.ide.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.completion.ml.CompletionMLPolicy

class RsCompletionMLPolicy : CompletionMLPolicy {
    override fun isReRankingDisabled(parameters: CompletionParameters): Boolean = true
}
