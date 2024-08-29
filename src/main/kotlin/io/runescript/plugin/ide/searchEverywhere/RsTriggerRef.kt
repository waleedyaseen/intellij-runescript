package io.runescript.plugin.ide.searchEverywhere

import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.text.StringUtil
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.triggerName
import io.runescript.plugin.lang.psi.type.trigger.RsTriggerType
import javax.swing.Icon

data class RsTriggerRef(val displayName: String, val icon: Icon?) {

    companion object {
        @JvmStatic
        fun forTrigger(trigger: RsTriggerType): RsTriggerRef = RsTriggerRef(
            trigger.literal,
            when (trigger) {
                RsTriggerType.PROC -> RsIcons.GutterProc
                RsTriggerType.CLIENTSCRIPT -> RsIcons.GutterClientScript
                RsTriggerType.COMMAND -> RsIcons.GutterCommand
                else -> RsIcons.GutterOther
            }
        )

        @JvmStatic
        fun forNavigationItem(item: NavigationItem): RsTriggerRef? {
            val trigger = RsTriggerType.lookup((item as RsScript).triggerName) ?: return null
            return forTrigger(trigger)
        }

        @JvmStatic
        fun forAllTriggers(): List<RsTriggerRef> {
            return RsTriggerType.values()
                .sortedWith { a, b -> StringUtil.naturalCompare(a.literal, b.literal) }
                .map { forTrigger(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RsTriggerRef

        return displayName == other.displayName
    }

    override fun hashCode(): Int {
        return displayName.hashCode()
    }
}