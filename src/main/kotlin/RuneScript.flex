package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import java.util.List;

import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.BAD_CHARACTER;
import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.SEMICOLON;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;

%%

%{
List<String> typeNames = List.of("int","string");
%}

%class _RuneScriptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

IDENTIFIER_PART = [a-zA-Z0-9_+]
IDENTIFIER = {IDENTIFIER_PART}+

%%
<YYINITIAL> {
{IDENTIFIER} {
  if (typeNames.contains(yytext())) {
    return TYPE_NAME;
  } else {
    return IDENTIFIER;
  }
}
"[" { return LBRACE; }
"]" { return RBRACE; }
"(" { return LPAREN; }
")" { return RPAREN; }
"," { return COMMA; }
";" { return SEMICOLON; }
"$" { return DOLLAR; }
[^] { return BAD_CHARACTER; }
}