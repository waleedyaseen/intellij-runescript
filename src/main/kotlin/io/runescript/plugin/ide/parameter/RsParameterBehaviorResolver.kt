package io.runescript.plugin.ide.parameter

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.ide.doc.findDoc
import io.runescript.plugin.lang.psi.RsArgumentList
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.arguments
import io.runescript.plugin.lang.psi.localModificationTracker
import io.runescript.plugin.lang.psi.stubIndexModificationTracker
import io.runescript.plugin.lang.stubs.index.RsScriptIndex

data class RsParameterBehavior(
    val id: String,
    val options: List<String>,
)

object RsParameterBehaviorResolver {
    private val CACHE_KEY =
        Key.create<CachedValue<Map<Int, List<RsParameterBehavior>>>>("io.runescript.plugin.parameterBehaviors")

    fun find(
        argument: RsExpression,
        behaviorId: String,
    ): RsParameterBehavior? = find(argument, setOf(behaviorId))

    fun find(
        argument: RsExpression,
        behaviorIds: Set<String>,
    ): RsParameterBehavior? {
        val argumentList = argument.parentOfType<RsArgumentList>() ?: return null
        val call = argumentList.parent as? RsCallExpression ?: return null
        val argumentIndex = call.arguments.indexOf(argument).takeIf { it >= 0 } ?: return null
        return behaviors(call)[argumentIndex]?.firstOrNull { behavior -> behavior.id in behaviorIds }
    }

    private fun behaviors(call: RsCallExpression): Map<Int, List<RsParameterBehavior>> {
        val callElement = call as PsiElement
        return CachedValuesManager
            .getManager(callElement.project)
            .getCachedValue(
                callElement,
                CACHE_KEY,
                { calculate(call) },
                false,
            )
    }

    private fun calculate(call: RsCallExpression): CachedValueProvider.Result<Map<Int, List<RsParameterBehavior>>> {
        val callElement = call as PsiElement
        val dependencies =
            mutableListOf<Any>(
                callElement.containingFile.localModificationTracker(),
                callElement.project.stubIndexModificationTracker(RsScriptIndex.KEY),
            )
        val target =
            callElement.reference?.resolve() as? RsScript
                ?: return CachedValueProvider.Result.create(emptyMap(), *dependencies.toTypedArray())
        dependencies += target.containingFile.localModificationTracker()

        val metadata =
            target
                .findDoc()
                ?.getAllSections()
                .orEmpty()
                .flatMap { section -> section.findTagsByName(PARAMETER_METADATA_TAG) }
                .groupBy { tag -> tag.getSubjectName() }
        val result =
            target
                .parameterList
                ?.parameterList
                .orEmpty()
                .mapIndexedNotNull { index, parameter ->
                    val name = parameter.localVariableExpression?.name ?: return@mapIndexedNotNull null
                    val behaviors = metadata[name].orEmpty().mapNotNull { tag -> parse(tag.getContent()) }
                    if (behaviors.isEmpty()) null else index to behaviors
                }.toMap()
        return CachedValueProvider.Result.create(result, *dependencies.toTypedArray())
    }

    private fun parse(content: String): RsParameterBehavior? {
        val match = BEHAVIOR_PATTERN.matchEntire(content.trim()) ?: return null
        val id = match.groupValues[1]
        val bracketOptions =
            match.groupValues[2]
                .split(',')
                .map(String::trim)
                .filter(String::isNotEmpty)
        val trailingOptions =
            match.groupValues[3]
                .split(WHITESPACE)
                .filter(String::isNotEmpty)
        return RsParameterBehavior(id, bracketOptions + trailingOptions)
    }

    private const val PARAMETER_METADATA_TAG = "parammeta"
    private val BEHAVIOR_PATTERN = Regex("([^\\s\\[]+)(?:\\[([^]]*)])?(?:\\s+(.*))?", RegexOption.DOT_MATCHES_ALL)
    private val WHITESPACE = "\\s+".toRegex()
}
