package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import io.runescript.plugin.ide.config.RsConfig;

import java.util.ArrayList;
import java.util.List;

import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.BAD_CHARACTER;
import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.SEMICOLON;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;

%%

%{
private Project project;
private List<String> _cachedPrimitiveTypeNames;

public List<String> getTypeNames() {
    if (_cachedPrimitiveTypeNames == null) {
        List<String> typeNames = RsConfig.INSTANCE.getPrimitiveTypes(project);
        _cachedPrimitiveTypeNames = new ArrayList<>(typeNames);
    }
    return _cachedPrimitiveTypeNames;
}
%}

%ctorarg Project project
%init{
this.project =  project;
%init}

%class _RuneScriptLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

IDENTIFIER_PART = [a-zA-Z0-9_+]
IDENTIFIER = {IDENTIFIER_PART}+

%%
<YYINITIAL> {

// Keywords
"if" { return IF; }
"while" { return WHILE; }

// General
{IDENTIFIER} {
  CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_NAME;
      }
  }
  return IDENTIFIER;
}

"$" { return DOLLAR; }

// Separators
"{" { return LBRACE; }
"}" { return RBRACE; }
"[" { return LBRACKET; }
"]" { return RBRACKET; }
"(" { return LPAREN; }
")" { return RPAREN; }
"," { return COMMA; }
";" { return SEMICOLON; }

// Ignored
[\ \t\r\n] { return TokenType.WHITE_SPACE; }
[^] { return BAD_CHARACTER; }
}