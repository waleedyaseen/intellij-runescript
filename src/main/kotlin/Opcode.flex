package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.List;

import static io.runescript.plugin.lang.psi.RsTokenTypes.*;
import static io.runescript.plugin.lang.psi.op.RsOpElementTypes.*;
%%

%{
private RsLexerInfo lexerInfo;

public List<String> getTypeNames() {
    return lexerInfo.getTypeNames();
}
%}

%ctorarg RsLexerInfo lexerInfo
%init{
this.lexerInfo =  lexerInfo;
%init}

%class _RsOpLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
SINGLE_LINE_COMMENT = "//"([^\r\n]*)(\r|\n|\r\n)
MULTI_LINE_COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/"
IDENTIFIER_PART = [a-zA-Z0-9_+\.]
IDENTIFIER = ({IDENTIFIER_PART})+
DECIMAL_DIGIT = [0-9]
HEX_DIGIT = [0-9a-fA-F]
HEX_INTEGER = 0[xX]({HEX_DIGIT})+
DECIMAL_INTEGER = ({DECIMAL_DIGIT})+
INTEGER = ({DECIMAL_INTEGER})|({HEX_INTEGER})

%%
<YYINITIAL> {
{MULTI_LINE_COMMENT} { return MULTI_LINE_COMMENT; }
{SINGLE_LINE_COMMENT} { return SINGLE_LINE_COMMENT; }
{INTEGER} { return INTEGER; }
{IDENTIFIER} {
  CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_LITERAL;
      }
  }
  return IDENTIFIER;
}

// Separators
"[" { return LBRACKET; }
"]" { return RBRACKET; }
"(" { return LPAREN; }
")" { return RPAREN; }
"," { return COMMA; }
"#" { return HASH; }
"$" { return DOLLAR; }
// Ignored
[\ \t\r\n] { return TokenType.WHITE_SPACE; }
[^] { return TokenType.BAD_CHARACTER; }
}