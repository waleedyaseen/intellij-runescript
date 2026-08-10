package io.runescript.plugin.ide.refactoring

import com.intellij.psi.search.searches.ReferencesSearch
import io.runescript.plugin.lang.psi.RsCallExpression
import io.runescript.plugin.lang.psi.RsScript

internal fun findScriptCalls(script: RsScript): List<RsCallExpression> =
    ReferencesSearch
        .search(script)
        .findAll()
        .mapNotNull { reference -> reference.element as? RsCallExpression }
        .distinct()
