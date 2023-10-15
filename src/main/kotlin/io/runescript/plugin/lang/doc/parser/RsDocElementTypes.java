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

import io.runescript.plugin.lang.doc.psi.impl.RsDocName;
import io.runescript.plugin.lang.doc.psi.impl.RsDocSection;
import io.runescript.plugin.lang.doc.psi.impl.RsDocTag;

public class RsDocElementTypes {
    public static final RsDocElementType RSDOC_SECTION = new RsDocElementType("RSDOC_SECTION", RsDocSection.class);
    public static final RsDocElementType RSDOC_TAG = new RsDocElementType("RSDOC_TAG", RsDocTag.class);
    public static final RsDocElementType RSDOC_NAME = new RsDocElementType("RSDOC_NAME", RsDocName.class);
}