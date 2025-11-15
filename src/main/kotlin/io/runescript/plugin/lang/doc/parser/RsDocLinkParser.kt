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
package io.runescript.plugin.lang.doc.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType
import io.runescript.plugin.ide.neptune.typeManagerOrDefault
import io.runescript.plugin.lang.lexer.RsLexerAdapter
import io.runescript.plugin.lang.lexer.RsLexerInfo
import io.runescript.plugin.lang.psi.RsElementTypes
import io.runescript.plugin.lang.psi.RsTokenTypes

/**
 * Parses the contents of a Markdown link in RSDoc. Uses the standard Kotlin lexer.
 */
class RsDocLinkParser : PsiParser {
    companion object {
        @JvmStatic
        fun parseMarkdownLink(root: IElementType, chameleon: ASTNode): ASTNode {
            val parentElement = chameleon.treeParent.psi
            val project = parentElement.project
            val builder = PsiBuilderFactory.getInstance().createBuilder(
                project,
                chameleon,
                RsLexerAdapter(RsLexerInfo(parentElement.typeManagerOrDefault)),
                root.language,
                chameleon.text
            )
            val parser = RsDocLinkParser()

            return parser.parse(root, builder).firstChildNode
        }
    }

    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        val hasLBracket = builder.tokenType == RsElementTypes.LBRACKET
        if (hasLBracket) {
            builder.advanceLexer()
        }
        parseQualifiedName(builder)
        if (hasLBracket) {
            if (!builder.eof() && builder.tokenType != RsElementTypes.RBRACKET) {
                builder.error("Closing bracket expected")
                while (!builder.eof() && builder.tokenType != RsElementTypes.RBRACKET) {
                    builder.advanceLexer()
                }
            }
            if (builder.tokenType == RsElementTypes.RBRACKET) {
                builder.advanceLexer()
            }
        } else {
            if (!builder.eof()) {
                builder.error("Expression expected")
                while (!builder.eof()) {
                    builder.advanceLexer()
                }
            }
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseQualifiedName(builder: PsiBuilder) {
        // [domain/name] or [name]
        var marker = builder.mark()
        while (true) {
            // don't highlight a word in a link as an error if it happens to be a Kotlin keyword
            if (!isName(builder.tokenType)) {
                marker.drop()
                builder.error("Identifier expected")
                break
            }
            builder.advanceLexer()
            marker.done(RsDocElementTypes.RSDOC_NAME)
            if (builder.tokenType != RsElementTypes.SLASH) {
                break
            }
            marker = marker.precede()
            builder.advanceLexer()
        }
    }

    private fun isName(tokenType: IElementType?) = tokenType in RsTokenTypes.ID_TOKENS
}