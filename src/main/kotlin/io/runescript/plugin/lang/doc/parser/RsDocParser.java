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

package io.runescript.plugin.lang.doc.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import io.runescript.plugin.lang.doc.lexer.RsDocTokens;
import org.jetbrains.annotations.NotNull;

public class RsDocParser implements PsiParser {
    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        if (builder.getTokenType() == RsDocTokens.START) {
            builder.advanceLexer();
        }
        PsiBuilder.Marker currentSectionMarker = builder.mark();

        // todo: parse RSDoc tags, markdown, etc...
        while (!builder.eof()) {
            if (builder.getTokenType() == RsDocTokens.TAG_NAME) {
                currentSectionMarker = parseTag(builder, currentSectionMarker);
            } else if (builder.getTokenType() == RsDocTokens.END) {
                if (currentSectionMarker != null) {
                    currentSectionMarker.done(RsDocElementTypes.RSDOC_SECTION);
                    currentSectionMarker = null;
                }
                builder.advanceLexer();
            } else {
                builder.advanceLexer();
            }
        }

        if (currentSectionMarker != null) {
            currentSectionMarker.done(RsDocElementTypes.RSDOC_SECTION);
        }
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private static PsiBuilder.Marker parseTag(PsiBuilder builder, PsiBuilder.Marker currentSectionMarker) {
        String tagName = builder.getTokenText();
        if (tagName == null) throw new IllegalStateException();
        RsDocKnownTag knownTag = RsDocKnownTag.Companion.findByTagName(tagName);
        if (knownTag != null && knownTag.isSectionStart()) {
            currentSectionMarker.done(RsDocElementTypes.RSDOC_SECTION);
            currentSectionMarker = builder.mark();
        }
        PsiBuilder.Marker tagStart = builder.mark();
        builder.advanceLexer();

        while (!builder.eof() && !isAtEndOfTag(builder)) {
            builder.advanceLexer();
        }
        tagStart.done(RsDocElementTypes.RSDOC_TAG);
        return currentSectionMarker;
    }

    private static boolean isAtEndOfTag(PsiBuilder builder) {
        if (builder.getTokenType() == RsDocTokens.END) {
            return true;
        }
        if (builder.getTokenType() == RsDocTokens.LEADING_ASTERISK) {
            int lookAheadCount = 1;
            if (builder.lookAhead(1) == RsDocTokens.TEXT) {
                lookAheadCount++;
            }
            return builder.lookAhead(lookAheadCount) == RsDocTokens.TAG_NAME;
        }
        return false;
    }
}