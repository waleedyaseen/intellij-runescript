package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import io.runescript.plugin.symbollang.psi.RsSymSymbol

object RsConstantInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        val name = (item.psiElement as? RsSymSymbol)?.name ?: item.lookupString.removePrefix("^")
        if (name.isBlank()) {
            return
        }
        RsInsertHandlerUtil.replaceAndMoveCaret(
            context,
            RsInsertHandlerUtil.findIdentifierStart(context, marker = '^'),
            "^$name",
        )
    }
}
