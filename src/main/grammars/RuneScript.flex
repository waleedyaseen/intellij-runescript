package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;

import java.util.List;

import static io.runescript.plugin.lang.psi.RsTokenTypes.*;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
%%

%{
private final RsLexerInfo lexerInfo;
private final IntStack statesStack = new IntArrayList();

public List<String> getTypeNames() {
    return lexerInfo.getTypeNames();
}

public void pushState(int state) {
    statesStack.push(yystate());
    yybegin(state);
}

public void popState() {
    yybegin(statesStack.pop());
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
LINE_COMMENT = "//"([^\r\n]*)(\r|\n|\r\n)
IDENTIFIER_PART = [a-zA-Z0-9_+\.:]
IDENTIFIER = ({IDENTIFIER_PART})+
DECIMAL_DIGIT = [0-9]
HEX_DIGIT = [0-9a-fA-F]
HEX_INTEGER = 0[xX]({HEX_DIGIT})+
DECIMAL_INTEGER = (({DECIMAL_DIGIT})+)
COORDGRID = {DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}
INTEGER = ([-+]?)(({DECIMAL_INTEGER})|({HEX_INTEGER}))
STRING_ESCAPE_SEQUENCE=\\([abfnrtv\'\"\\])
STRING_PART = [^\"\r\n<\\]+

COLOR_TAG = "<"(shad|col|str|u)"="([0-9a-fA-F]+)">"
OTHER_TAG = "<"(str|u|br|lt|gt)">"
IMG_TAG = "<img="([0-9]+)">"
CLOSE_TAG = "</"(shad|col|str|u)">"
INCOMPLETE_TAG = "<"(shad|col|str|u|img)"="

%x STRING, STRING_INTERPOLATION, BLOCK_COMMENT

%%

<BLOCK_COMMENT> {
"*/" {
    popState();
    return BLOCK_COMMENT;
}
<<EOF>> {
    popState();
    return BLOCK_COMMENT;
}
[\s\S] { }
}

<YYINITIAL,STRING_INTERPOLATION> {

// Comments
"/**/" { return BLOCK_COMMENT; }
"/*" { pushState(BLOCK_COMMENT); }

{LINE_COMMENT} { return LINE_COMMENT; }
// Keywords
"if" { return IF; }
"else" { return ELSE; }
"while" { return WHILE; }
"true" { return TRUE; }
"false" { return FALSE; }
"null" { return NULL; }
"case" { return CASE; }
"default" { return DEFAULT; }
"calc" { return CALC; }
"return" { return RETURN; }
\" { pushState(STRING); return STRING_START; }

{COORDGRID} { return COORDGRID; }
{INTEGER} { return INTEGER; }


// Operators
"~" { return TILDE; }
"=" { return EQUAL; }
"<" { return LT; }
">" {
    if (yystate() == STRING_INTERPOLATION) {
        popState();
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

// General
{IDENTIFIER} {
  CharSequence lexeme = yytext();
  for (String typeName: getTypeNames()) {
      if (typeName.contentEquals(lexeme)) {
          return TYPE_LITERAL;
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
          return ARRAY_TYPE_LITERAL;
      }
  }
  return IDENTIFIER;
}

// Ignored
[\ \t\r\n] { return TokenType.WHITE_SPACE; }
[^] { return TokenType.BAD_CHARACTER; }
}
<STRING> {
{STRING_ESCAPE_SEQUENCE} { return STRING_PART; }
\" { popState(); return STRING_END; }
({OTHER_TAG}|{CLOSE_TAG}|{COLOR_TAG}|{IMG_TAG}) { return STRING_TAG; }
{INCOMPLETE_TAG} { return STRING_PART; }
"<" { pushState(STRING_INTERPOLATION); return STRING_INTERPOLATION_START; }
{STRING_PART} { return STRING_PART; }
[^] { return TokenType.BAD_CHARACTER; }
}