package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.project.Project;
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
{IDENTIFIER} {
  CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_NAME;
      }
  }
  return IDENTIFIER;
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