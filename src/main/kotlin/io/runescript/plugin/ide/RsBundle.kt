package io.runescript.plugin.ide

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey


object RsBundle {
    private const val BUNDLE_FQN: @NonNls String = "messages.RsBundle"
    private val BUNDLE = DynamicBundle(RsBundle::class.java, BUNDLE_FQN)

    fun message(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): @Nls String {
        return BUNDLE.getMessage(key, *params)
    }

    fun pointer(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): () -> String {
        return { message(key, params) }
    }
}