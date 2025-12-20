package io.runescript.plugin.ide.searchEverywhere

import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.text.StringUtil
import io.runescript.plugin.ide.RsIcons
import io.runescript.plugin.ide.neptune.triggerManager
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.triggerName
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import javax.swing.Icon

data class RsTriggerRef(val displayName: String, val icon: Icon?) {

    companion object {
        private val triggerTypes = arrayListOf(
            "proc",
            "clientscript"
        )

        @JvmStatic
        fun forTrigger(trigger: TriggerType): RsTriggerRef = forTrigger(trigger.identifier)

        @JvmStatic
        fun forTrigger(trigger: String): RsTriggerRef = RsTriggerRef(
            trigger,
            when (trigger) {
                "proc" -> RsIcons.GutterProc
                "clientscript" -> RsIcons.GutterClientScript
                "command" -> RsIcons.GutterCommand
                else -> RsIcons.GutterOther
            }
        )

        @JvmStatic
        fun forNavigationItem(item: NavigationItem): RsTriggerRef? {
            val triggerName = (item as RsScript).triggerName
            val trigger = item.triggerManager.findOrNull(triggerName) ?: return null
            return forTrigger(trigger)
        }

        @JvmStatic
        fun forAllTriggers(): List<RsTriggerRef> {
            return triggerTypes
                .sortedWith { a, b -> StringUtil.naturalCompare(a, b) }
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