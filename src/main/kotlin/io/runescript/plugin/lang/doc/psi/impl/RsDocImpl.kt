/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package io.runescript.plugin.lang.doc.psi.impl

import com.intellij.lang.Language
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.RuneScript
import io.runescript.plugin.lang.doc.getChildOfType
import io.runescript.plugin.lang.doc.getChildrenOfType
import io.runescript.plugin.lang.doc.lexer.RsDocTokens
import io.runescript.plugin.lang.doc.parser.RsDocKnownTag
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsTokenTypes

class RsDocImpl(buffer: CharSequence?) : LazyParseablePsiElement(RsDocTokens.RSDOC, buffer), RsDoc {

    override fun getLanguage(): Language = RuneScript

    override fun toString(): String = node.elementType.toString()

    override fun getTokenType(): IElementType = RsTokenTypes.DOC_COMMENT

    override fun getOwner(): RsScript? {
        return parentOfType<RsScript>()
    }

    override fun getDefaultSection(): RsDocSection = getChildOfType()!!

    override fun getAllSections(): List<RsDocSection> =
        getChildrenOfType<RsDocSection>().toList()

    override fun findSectionByName(name: String): RsDocSection? =
        getChildrenOfType<RsDocSection>().firstOrNull { it.name == name }

    override fun findSectionByTag(tag: RsDocKnownTag): RsDocSection? =
        findSectionByName(tag.name.lowercase())

    override fun findSectionByTag(tag: RsDocKnownTag, subjectName: String): RsDocSection? =
        getChildrenOfType<RsDocSection>().firstOrNull {
            it.name == tag.name.lowercase() && it.getSubjectName() == subjectName
        }
}