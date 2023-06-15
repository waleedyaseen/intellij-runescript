package io.runescript.plugin.oplang.psi

import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.RuneScript
import org.jetbrains.annotations.NonNls

class RsOpElementType(debugName: @NonNls String) : IElementType(debugName, RuneScript)