package io.runescript.plugin.symbollang.psi

import com.intellij.psi.tree.IElementType
import io.runescript.plugin.symbollang.RuneScriptSymbol
import org.jetbrains.annotations.NonNls

class RsSymElementType(@NonNls debugName: String) : IElementType(debugName, RuneScriptSymbol)