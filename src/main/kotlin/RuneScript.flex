package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import io.runescript.plugin.ide.config.RsConfig;

import java.util.ArrayList;
import java.util.List;

import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.BAD_CHARACTER;
import static io.runescript.plugin.lang.psi.RuneScriptTokenTypes.STRING_LITERAL;
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
IDENTIFIER = ({IDENTIFIER_PART})+
DECIMAL_DIGIT = [0-9]
HEX_DIGIT = [0-9a-fA-F]
HEX_INTEGER = 0[xX]({HEX_DIGIT})+
DECIMAL_INTEGER = ({DECIMAL_DIGIT})+
INTEGER = ({DECIMAL_INTEGER})|({HEX_INTEGER})
STRING_PART = [^\"\r\n<]+
COLOR_TAG = "<col="([0-9a-fA-F]+)">"

%x STRING, STRING_INTERPOLATION

%%
<YYINITIAL,STRING_INTERPOLATION> {

// Keywords
"if" { return IF; }
"while" { return WHILE; }
"true" { return TRUE; }
"false" { return FALSE; }
"null" { return NULL; }
"case" { return CASE; }
\" { yybegin(STRING); return STRING_START; }

{INTEGER} { return INTEGER; }

// General
{IDENTIFIER} {
  CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_NAME;
      }
       if (lexeme.length() > 4
              && lexeme.charAt(0) == 'd'
              && lexeme.charAt(1) == 'e'
              && lexeme.charAt(2) == 'f'
              && lexeme.charAt(3) == '_'
              && typeName.contentEquals(lexeme.subSequence(4, lexeme.length()))) {
          return DEFINE_TYPE;
      }
       if (lexeme.length() > 7
              && lexeme.charAt(0) == 's'
              && lexeme.charAt(1) == 'w'
              && lexeme.charAt(2) == 'i'
              && lexeme.charAt(3) == 't'
              && lexeme.charAt(4) == 'c'
              && lexeme.charAt(5) == 'h'
              && lexeme.charAt(6) == '_'
              && typeName.contentEquals(lexeme.subSequence(7, lexeme.length()))) {
          return SWITCH;
      }
  }
  return IDENTIFIER;
}
">" {
    if (yystate() == STRING_INTERPOLATION) {
        yybegin(STRING);
        return STRING_INTERPOLATION_END;
    } else {
        throw new RuntimeException();
    }
}
"$" { return DOLLAR; }

// Operators
"=" { return EQUAL; }
"~" { return TILDE; }

// Separators
"{" { return LBRACE; }
"}" { return RBRACE; }
"[" { return LBRACKET; }
"]" { return RBRACKET; }
"(" { return LPAREN; }
")" { return RPAREN; }
"," { return COMMA; }
":" { return COLON; }
";" { return SEMICOLON; }

// Ignored
[\ \t\r\n] { return TokenType.WHITE_SPACE; }
[^] { return BAD_CHARACTER; }
}
<STRING> {
\" { yybegin(YYINITIAL); return STRING_END; }
"<br>" { return STRING_TAG; }
"</col>" { return STRING_TAG; }
{COLOR_TAG} { return STRING_TAG; }
"<col=" { return STRING_PART; }
"<" { yybegin(STRING_INTERPOLATION); return STRING_INTERPOLATION_START; }
{STRING_PART} { return STRING_PART; }
[^] { return BAD_CHARACTER; }
}