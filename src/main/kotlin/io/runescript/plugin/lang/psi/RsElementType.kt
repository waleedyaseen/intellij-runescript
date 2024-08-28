package io.runescript.plugin.lang.psi

import com.intellij.psi.tree.IElementType
import io.runescript.plugin.lang.RuneScript
import org.jetbrains.annotations.NonNls

class RsElementType(@NonNls debugName: String) : IElementType(debugName, RuneScript)