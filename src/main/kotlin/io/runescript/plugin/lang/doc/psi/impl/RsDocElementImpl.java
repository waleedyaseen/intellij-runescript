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

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import io.runescript.plugin.lang.RuneScript;
import io.runescript.plugin.lang.doc.psi.api.RsDocElement;
import org.jetbrains.annotations.NotNull;

public abstract class RsDocElementImpl extends ASTWrapperPsiElement implements RsDocElement {
    @NotNull
    @Override
    public Language getLanguage() {
        return RuneScript.INSTANCE;
    }

    @Override
    public String toString() {
        return getNode().getElementType().toString();
    }

    public RsDocElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}