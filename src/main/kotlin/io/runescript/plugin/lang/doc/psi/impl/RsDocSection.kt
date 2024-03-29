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
package io.runescript.plugin.lang.doc.psi.impl

import com.intellij.lang.ASTNode
import io.runescript.plugin.lang.doc.getChildrenOfType

/**
 * The part of a doc comment which describes a single class, method or property
 * produced by the element being documented. For example, the doc comment of a class
 * can have sections for the class itself, its primary constructor and each of the
 * properties defined in the primary constructor.
 */
class RsDocSection(node: ASTNode) : RsDocTag(node) {
    /**
     * Returns the name of the section (the name of the doc tag introducing the section,
     * or null for the default section).
     */
    override fun getName(): String? =
        (firstChild as? RsDocTag)?.name

    override fun getSubjectName(): String? =
        (firstChild as? RsDocTag)?.getSubjectName()

    override fun getContent(): String =
        (firstChild as? RsDocTag)?.getContent() ?: super.getContent()

    fun findTagsByName(name: String): List<RsDocTag> {
        return getChildrenOfType<RsDocTag>().filter { it.name == name }
    }
}