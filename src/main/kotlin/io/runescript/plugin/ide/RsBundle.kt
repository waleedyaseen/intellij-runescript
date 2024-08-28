package io.runescript.plugin.ide

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey


object RsBundle {
    @NonNls private const val BUNDLE_FQN: String = "messages.RsBundle"
    private val BUNDLE = DynamicBundle(RsBundle::class.java, BUNDLE_FQN)

    @Nls
    fun message(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): String {
        return BUNDLE.getMessage(key, *params)
    }

    fun pointer(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): () -> String {
        return { message(key, params) }
    }
}