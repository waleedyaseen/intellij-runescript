/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package io.runescript.plugin.lang.doc.psi.api

import com.intellij.psi.PsiDocCommentBase
import io.runescript.plugin.lang.doc.parser.RsDocKnownTag
import io.runescript.plugin.lang.doc.psi.impl.RsDocSection
import io.runescript.plugin.lang.psi.RsScript

interface RsDoc : PsiDocCommentBase, RsDocElement {
    override fun getOwner(): RsScript?
    fun getDefaultSection(): RsDocSection
    fun getAllSections(): List<RsDocSection>
    fun findSectionByName(name: String): RsDocSection?
    fun findSectionByTag(tag: RsDocKnownTag): RsDocSection?
    fun findSectionByTag(tag: RsDocKnownTag, subjectName: String): RsDocSection?
}