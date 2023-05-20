package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import java.util.List;

import static io.runescript.plugin.lang.psi.RsTokenTypes.*;
import static io.runescript.plugin.lang.psi.RsTypes.*;
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

%class _RsLexer
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
STRING_PART = [^\"\r\n<]+
COLOR_TAG = "<col="([0-9a-fA-F]+)">"

%x STRING, STRING_INTERPOLATION

%%
<YYINITIAL,STRING_INTERPOLATION> {
{MULTI_LINE_COMMENT} { return MULTI_LINE_COMMENT; }
{SINGLE_LINE_COMMENT} { return SINGLE_LINE_COMMENT; }
// Keywords
"if" { return IF; }
"while" { return WHILE; }
"true" { return TRUE; }
"false" { return FALSE; }
"null" { return NULL; }
"case" { return CASE; }
"default" { return DEFAULT; }
"calc" { return CALC; }
"return" { return RETURN; }
\" { yybegin(STRING); return STRING_START; }

{INTEGER} { return INTEGER; }


// Operators
"~" { return TILDE; }
"=" { return EQUAL; }
"<" { return LT; }
">" {
    if (yystate() == STRING_INTERPOLATION) {
        yybegin(STRING);
        return STRING_INTERPOLATION_END;
    } else {
        return GT;
    }
}
">=" { return GTE; }
"<=" { return LTE; }
"!" { return EXCEL;}
"+" { return PLUS; }
"-" { return MINUS; }
"*" { return STAR; }
"/" { return SLASH; }
"%" { return PERCENT; }
"&" { return AMPERSAND; }
"|" { return BAR; }
"$" { return DOLLAR; }

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
       int length = typeName.length();
       if (lexeme.length() == length + 5
              && lexeme.charAt(length) == 'a'
              && lexeme.charAt(length + 1) == 'r'
              && lexeme.charAt(length + 2) == 'r'
              && lexeme.charAt(length + 3) == 'a'
              && lexeme.charAt(length + 4) == 'y'
              && typeName.contentEquals(lexeme.subSequence(0, length))) {
          return ARRAY_TYPE_NAME;
      }
  }
  return IDENTIFIER;
}

// Separators
"^" { return CARET; }
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
[^] { return TokenType.BAD_CHARACTER; }
}
<STRING> {
\" { yybegin(YYINITIAL); return STRING_END; }
"<br>" { return STRING_TAG; }
"</col>" { return STRING_TAG; }
{COLOR_TAG} { return STRING_TAG; }
"<col=" { return STRING_PART; }
"<" { yybegin(STRING_INTERPOLATION); return STRING_INTERPOLATION_START; }
{STRING_PART} { return STRING_PART; }
[^] { return TokenType.BAD_CHARACTER; }
}