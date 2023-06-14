package io.runescript.plugin.symbollang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.List;

import static io.runescript.plugin.symbollang.psi.RsSymElementTypes.*;
%%

%class _RsSymLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%%
<YYINITIAL> {
    [^\t\n]* { return STRING; }
    "\t" { return TAB; }
    "\n" { return NEW_LINE; }
    [^] { return TokenType.BAD_CHARACTER; }
}