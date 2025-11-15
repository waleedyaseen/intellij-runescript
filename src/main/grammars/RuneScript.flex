package io.runescript.plugin.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import io.runescript.plugin.lang.psi.RsTokenTypes;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntStack;

import java.util.List;

import static io.runescript.plugin.lang.psi.RsTokenTypes.*;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
%%

%{
private final RsLexerInfo lexerInfo;
private final IntStack statesStack = new IntArrayList();

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
IDENTIFIER_PART = [a-zA-Z0-9_\.:]
IDENTIFIER = ({IDENTIFIER_PART})+
DECIMAL_DIGIT = [0-9]
HEX_DIGIT = [0-9a-fA-F]
HEX_INTEGER = 0[xX]({HEX_DIGIT})+
LONG_HEX_INTEGER = 0[xX]({HEX_DIGIT})+ [lL]
DECIMAL_INTEGER = (({DECIMAL_DIGIT})+)
LONG_DECIMAL_INTEGER = (({DECIMAL_DIGIT})+) [lL]
COORDGRID = {DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}_{DECIMAL_INTEGER}
INTEGER = ([-+]?)(({DECIMAL_INTEGER})|({HEX_INTEGER}))
LONG = ([-+]?)(({LONG_DECIMAL_INTEGER})|({LONG_HEX_INTEGER}))
STRING_ESCAPE_SEQUENCE=\\([abfnrtv\'\"\\<])
STRING_PART = [^\"\r\n<\\]+
INCREMENT = "++"
DECREMENT = "--"

COLOR_TAG = "<"(shad|col|str|u)"="([0-9a-fA-F]+)">"
OTHER_TAG = "<"(str|u|br|lt|gt)">"
IMG_TAG = "<img="([0-9]+)">"
CLOSE_TAG = "</"(shad|col|str|u)">"
INCOMPLETE_TAG = "<"(shad|col|str|u|img)"="

%x STRING, STRING_INTERPOLATION, BLOCK_COMMENT, DOC_COMMENT

%%

<BLOCK_COMMENT, DOC_COMMENT> {
"*/" {
    int state = yystate();
    popState();
    return state == DOC_COMMENT ? RsTokenTypes.DOC_COMMENT : RsTokenTypes.BLOCK_COMMENT;
}
<<EOF>> {
    int state = yystate();
    popState();
    return state == DOC_COMMENT ? RsTokenTypes.DOC_COMMENT : RsTokenTypes.BLOCK_COMMENT;
}
[\s\S] { }
}

<YYINITIAL,STRING_INTERPOLATION> {

// Comments
"/**/" { return RsTokenTypes.BLOCK_COMMENT; }
"/**" { pushState(DOC_COMMENT); }
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
{LONG} { return LONG; }
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
  if (lexerInfo.getTypeManager().getTypeKeywords().contains(lexeme)) {
    return TYPE_LITERAL;
  }
  if (lexerInfo.getTypeManager().getDefineKeywords().contains(lexeme)) {
    return DEFINE_TYPE;
  }
  if (lexerInfo.getTypeManager().getSwitchKeywords().contains(lexeme)) {
    return SWITCH;
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