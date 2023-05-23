package io.runescript.plugin.lang.psi

import com.intellij.psi.util.parentOfType

val RsStatement.controlFlowHolder: RsControlFlowHolder?
    get() = parentOfType<RsControlFlowHolder>()