package io.runescript.plugin.ide

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier


object RuneScriptBundle {
    private const val BUNDLE_FQN: @NonNls String = "messages.RuneScriptBundle"
    private val BUNDLE = DynamicBundle(RuneScriptBundle::class.java, BUNDLE_FQN)

    fun message(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): @Nls String {
        return BUNDLE.getMessage(key, *params)
    }
}