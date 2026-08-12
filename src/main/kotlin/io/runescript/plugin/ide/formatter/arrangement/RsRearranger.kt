package io.runescript.plugin.ide.formatter.arrangement

import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.arrangement.ArrangementEntry
import com.intellij.psi.codeStyle.arrangement.ArrangementSettings
import com.intellij.psi.codeStyle.arrangement.ArrangementSettingsSerializer
import com.intellij.psi.codeStyle.arrangement.DefaultArrangementEntry
import com.intellij.psi.codeStyle.arrangement.DefaultArrangementSettingsSerializer
import com.intellij.psi.codeStyle.arrangement.NameAwareArrangementEntry
import com.intellij.psi.codeStyle.arrangement.Rearranger
import com.intellij.psi.codeStyle.arrangement.TypeAwareArrangementEntry
import com.intellij.psi.codeStyle.arrangement.match.ArrangementEntryMatcher
import com.intellij.psi.codeStyle.arrangement.match.StdArrangementEntryMatcher
import com.intellij.psi.codeStyle.arrangement.match.StdArrangementMatchRule
import com.intellij.psi.codeStyle.arrangement.model.ArrangementAtomMatchCondition
import com.intellij.psi.codeStyle.arrangement.model.ArrangementMatchCondition
import com.intellij.psi.codeStyle.arrangement.std.ArrangementSettingsToken
import com.intellij.psi.codeStyle.arrangement.std.ArrangementStandardSettingsAware
import com.intellij.psi.codeStyle.arrangement.std.CompositeArrangementSettingsToken
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementSettings
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementSettingsToken
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokenType
import com.intellij.psi.codeStyle.arrangement.std.StdArrangementTokens
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName

class RsRearranger :
    Rearranger<RsArrangementEntry>,
    ArrangementStandardSettingsAware {
    override fun parseWithNew(
        root: PsiElement,
        document: Document?,
        ranges: MutableCollection<out TextRange>,
        newElement: PsiElement,
        settings: ArrangementSettings,
    ): Pair<RsArrangementEntry, MutableList<RsArrangementEntry>>? {
        val entries = parse(root, document, ranges, settings)
        val newScript = PsiTreeUtil.getParentOfType(newElement, RsScript::class.java, false)
        val newEntry = entries.firstOrNull { it.script === newScript } ?: return null
        return Pair.create(newEntry, entries)
    }

    override fun parse(
        root: PsiElement,
        document: Document?,
        ranges: MutableCollection<out TextRange>,
        settings: ArrangementSettings,
    ): MutableList<RsArrangementEntry> =
        PsiTreeUtil
            .getChildrenOfTypeAsList(root, RsScript::class.java)
            .filter { script -> ranges.any { it.intersects(script.textRange) } }
            .mapTo(mutableListOf(), ::RsArrangementEntry)

    override fun getBlankLines(
        settings: CodeStyleSettings,
        parent: RsArrangementEntry?,
        previous: RsArrangementEntry?,
        target: RsArrangementEntry,
    ): Int =
        if (previous == null) {
            0
        } else {
            settings.getCommonSettings(RuneScript).BLANK_LINES_AROUND_METHOD
        }

    override fun getSerializer(): ArrangementSettingsSerializer = SERIALIZER

    override fun getDefaultSettings(): StdArrangementSettings = DEFAULT_SETTINGS

    override fun getSupportedGroupingTokens(): List<CompositeArrangementSettingsToken> = emptyList()

    override fun getSupportedMatchingTokens(): List<CompositeArrangementSettingsToken> =
        listOf(
            CompositeArrangementSettingsToken(StdArrangementTokens.General.TYPE, SCRIPT),
            CompositeArrangementSettingsToken(StdArrangementTokens.Regexp.NAME),
            CompositeArrangementSettingsToken(
                StdArrangementTokens.General.ORDER,
                StdArrangementTokens.Order.KEEP,
                StdArrangementTokens.Order.BY_NAME,
            ),
        )

    override fun isEnabled(
        token: ArrangementSettingsToken,
        current: ArrangementMatchCondition?,
    ): Boolean = token in ENABLED_TOKENS

    override fun buildMatcher(condition: ArrangementMatchCondition): ArrangementEntryMatcher = StdArrangementEntryMatcher(condition)

    override fun getMutexes(): Collection<Set<ArrangementSettingsToken>> =
        listOf(setOf(StdArrangementTokens.Order.KEEP, StdArrangementTokens.Order.BY_NAME))

    companion object {
        @JvmField
        val SCRIPT: StdArrangementSettingsToken =
            StdArrangementSettingsToken.token(
                "RUNESCRIPT_SCRIPT",
                "script",
                StdArrangementTokenType.ENTRY_TYPE,
            )

        private val DEFAULT_SETTINGS =
            StdArrangementSettings.createByMatchRules(
                emptyList(),
                listOf(
                    StdArrangementMatchRule(
                        StdArrangementEntryMatcher(ArrangementAtomMatchCondition(SCRIPT)),
                        StdArrangementTokens.Order.BY_NAME,
                    ),
                ),
            )

        private val SERIALIZER =
            DefaultArrangementSettingsSerializer(
                { id -> SCRIPT.takeIf { it.id == id } },
                DEFAULT_SETTINGS,
            )

        private val ENABLED_TOKENS =
            setOf(
                SCRIPT,
                StdArrangementTokens.Regexp.NAME,
                StdArrangementTokens.Order.KEEP,
                StdArrangementTokens.Order.BY_NAME,
            )
    }
}

class RsArrangementEntry(
    val script: RsScript,
) : DefaultArrangementEntry(null, script.textRange.startOffset, script.textRange.endOffset, true),
    NameAwareArrangementEntry,
    TypeAwareArrangementEntry {
    override fun getName(): String = script.qualifiedName

    override fun getTypes(): Set<ArrangementSettingsToken> = setOf(RsRearranger.SCRIPT)
}
