package io.runescript.plugin.lang.psi.typechecker.trigger

import io.runescript.plugin.lang.psi.typechecker.type.Type

data object CommandTrigger : TriggerType {
    override val id: Int = -1
    override val identifier: String = "command"
    override val subjectMode: SubjectMode = SubjectMode.Name
    override val allowParameters: Boolean = true
    override val parameters: Type? = null
    override val allowReturns: Boolean = true
    override val returns: Type? = null
}
