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
package io.runescript.plugin.lang.doc.lexer;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.ILazyParseableElementType;
import com.intellij.psi.tree.TokenSet;
import io.runescript.plugin.lang.RuneScript;
import io.runescript.plugin.lang.doc.parser.RsDocLinkParser;
import io.runescript.plugin.lang.doc.parser.RsDocParser;
import io.runescript.plugin.lang.doc.psi.impl.RsDocImpl;
import org.jetbrains.annotations.Nullable;

public interface RsDocTokens {
    ILazyParseableElementType RSDOC = new ILazyParseableElementType("RSDoc", RuneScript.INSTANCE) {
        @Override
        public ASTNode parseContents(ASTNode chameleon) {
            PsiElement parentElement = chameleon.getTreeParent().getPsi();
            Project project = parentElement.getProject();
            PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new RsDocLexer(), getLanguage(),
                    chameleon.getText());
            PsiParser parser = new RsDocParser();

            return parser.parse(this, builder).getFirstChildNode();
        }

        @Nullable
        @Override
        public ASTNode createNode(CharSequence text) {
            return new RsDocImpl(text);
        }
    };

    int START_Id = 0;
    int END_Id = 1;
    int LEADING_ASTERISK_Id = 2;
    int TEXT_Id = 3;
    int CODE_BLOCK_TEXT_Id = 4;
    int TAG_NAME_Id = 5;
    int MARKDOWN_ESCAPED_CHAR_Id = 6;
    int MARKDOWN_INLINE_LINK_Id = 7;

    RsDocToken START = new RsDocToken("RSDOC_START", START_Id);
    RsDocToken END = new RsDocToken("RSDOC_END", END_Id);
    RsDocToken LEADING_ASTERISK = new RsDocToken("RSDOC_LEADING_ASTERISK", LEADING_ASTERISK_Id);

    RsDocToken TEXT = new RsDocToken("RSDOC_TEXT", TEXT_Id);
    RsDocToken CODE_BLOCK_TEXT = new RsDocToken("RSDOC_CODE_BLOCK_TEXT", CODE_BLOCK_TEXT_Id);

    RsDocToken TAG_NAME = new RsDocToken("RSDOC_TAG_NAME", TAG_NAME_Id);
    ILazyParseableElementType MARKDOWN_LINK = new ILazyParseableElementType("RSDOC_MARKDOWN_LINK", RuneScript.INSTANCE) {
        @Override
        public ASTNode parseContents(ASTNode chameleon) {
            return RsDocLinkParser.parseMarkdownLink(this, chameleon);
        }
    };

    RsDocToken MARKDOWN_ESCAPED_CHAR = new RsDocToken("RSDOC_MARKDOWN_ESCAPED_CHAR", MARKDOWN_ESCAPED_CHAR_Id);
    RsDocToken MARKDOWN_INLINE_LINK = new RsDocToken("RSDOC_MARKDOWN_INLINE_LINK", MARKDOWN_INLINE_LINK_Id);
    @SuppressWarnings("unused")
    TokenSet RSDOC_HIGHLIGHT_TOKENS = TokenSet.create(START, END, LEADING_ASTERISK, TEXT, CODE_BLOCK_TEXT, MARKDOWN_LINK, MARKDOWN_ESCAPED_CHAR, MARKDOWN_INLINE_LINK);
    TokenSet CONTENT_TOKENS = TokenSet.create(TEXT, CODE_BLOCK_TEXT, TAG_NAME, MARKDOWN_LINK, MARKDOWN_ESCAPED_CHAR, MARKDOWN_INLINE_LINK);
}