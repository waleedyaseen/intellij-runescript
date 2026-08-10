package io.runescript.plugin.ide.codeInsight.unwrap

import com.intellij.codeInsight.unwrap.UnwrapDescriptorBase
import com.intellij.codeInsight.unwrap.Unwrapper

class RsUnwrapDescriptor : UnwrapDescriptorBase() {
    override fun createUnwrappers(): Array<Unwrapper> =
        arrayOf(
            RsIfUnwrapper(),
            RsWhileUnwrapper(),
        )
}
