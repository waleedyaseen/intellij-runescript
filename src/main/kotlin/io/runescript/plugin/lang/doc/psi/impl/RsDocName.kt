/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.runescript.plugin.lang.doc.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import io.runescript.plugin.ide.doc.RsDocReference
import io.runescript.plugin.lang.doc.getStrictParentOfType
import io.runescript.plugin.lang.doc.psi.api.RsDoc
import io.runescript.plugin.lang.psi.RsElementTypes

/**
 * A single part of a qualified name in the tag subject or link.
 */
class RsDocName(node: ASTNode) : ASTWrapperPsiElement(node) {

    override fun getReference(): PsiReference? {
        if (parent !is RsDocName) {
            return RsDocReference(this)
        }
        return null
    }

    fun getContainingDoc(): RsDoc {
        val rsdoc = getStrictParentOfType<RsDoc>()
        return rsdoc ?: throw IllegalStateException("RsDocName must be inside a RsDoc")
    }

    fun getContainingSection(): RsDocSection {
        val rsdoc = getStrictParentOfType<RsDocSection>()
        return rsdoc ?: throw IllegalStateException("RsDocName must be inside a RsDocSection")
    }

    /**
     * Returns the range within the element containing the name (in other words,
     * the range of the element excluding the qualifier and dot, if present).
     */
    fun getNameTextRange(): TextRange {
        val dot = node.findChildByType(RsElementTypes.SLASH)
        val textRange = textRange
        val nameStart = if (dot != null) dot.textRange.endOffset - textRange.startOffset else 0
        return TextRange(nameStart, textRange.length)
    }

    fun getNameText(): String = getNameTextRange().substring(text)
}