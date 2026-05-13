package io.runescript.plugin.ide.completion.insertHandler

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement

object RsOperatorInsertHandler : InsertHandler<LookupElement> {
    override fun handleInsert(
        context: InsertionContext,
        item: LookupElement,
    ) {
        RsInsertHandlerUtil.replaceAndMoveCaret(
            context,
            RsInsertHandlerUtil.findOperatorStart(context),
            " ${item.lookupString} ",
        )
    }
}
