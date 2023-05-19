// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.runescript.plugin.lang.psi.RuneScriptTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RuneScriptParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return script_file(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(BRACED_BLOCK_STATEMENT, IF_STATEMENT, STATEMENT, WHILE_STATEMENT),
    create_token_set_(DYNAMIC_EXPRESSION, EXPRESSION, LOCAL_VARIABLE_EXPRESSION, PAR_EXPRESSION),
  };

  /* ********************************************************** */
  // LBRACE statement_list RBRACE
  public static boolean braced_block_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "braced_block_statement")) return false;
    if (!nextTokenIs(b, "<statement>", LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BRACED_BLOCK_STATEMENT, "<statement>");
    r = consumeToken(b, LBRACE);
    r = r && statement_list(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // name_expression
  public static boolean dynamic_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_EXPRESSION, "<expression>");
    r = name_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // par_expression
  //              | local_variable_expression
  //              | dynamic_expression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<expression>");
    r = par_expression(b, l + 1);
    if (!r) r = local_variable_expression(b, l + 1);
    if (!r) r = dynamic_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IF LPAREN expression RPAREN braced_block_statement
  public static boolean if_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_statement")) return false;
    if (!nextTokenIs(b, "<statement>", IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && braced_block_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DOLLAR name_expression
  public static boolean local_variable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_EXPRESSION, "<expression>");
    r = consumeToken(b, DOLLAR);
    r = r && name_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | WHILE | IF
  static boolean name_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, WHILE);
    if (!r) r = consumeToken(b, IF);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPAREN expression RPAREN
  public static boolean par_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "par_expression")) return false;
    if (!nextTokenIs(b, "<expression>", LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PAR_EXPRESSION, "<expression>");
    r = consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TYPE_NAME DOLLAR IDENTIFIER
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, TYPE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TYPE_NAME, DOLLAR, IDENTIFIER);
    exit_section_(b, m, PARAMETER, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN (parameter (COMMA parameter)*)? RPAREN
  public static boolean parameter_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && parameter_list_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, PARAMETER_LIST, r);
    return r;
  }

  // (parameter (COMMA parameter)*)?
  private static boolean parameter_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1")) return false;
    parameter_list_1_0(b, l + 1);
    return true;
  }

  // parameter (COMMA parameter)*
  private static boolean parameter_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter(b, l + 1);
    r = r && parameter_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA parameter)*
  private static boolean parameter_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_list_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_list_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA parameter
  private static boolean parameter_list_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_list_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LPAREN (TYPE_NAME (COMMA TYPE_NAME)*)? RPAREN
  public static boolean return_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_list")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && return_list_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, RETURN_LIST, r);
    return r;
  }

  // (TYPE_NAME (COMMA TYPE_NAME)*)?
  private static boolean return_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_list_1")) return false;
    return_list_1_0(b, l + 1);
    return true;
  }

  // TYPE_NAME (COMMA TYPE_NAME)*
  private static boolean return_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_NAME);
    r = r && return_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA TYPE_NAME)*
  private static boolean return_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_list_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!return_list_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_list_1_0_1", c)) break;
    }
    return true;
  }

  // COMMA TYPE_NAME
  private static boolean return_list_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_list_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMA, TYPE_NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // script_header statement_list
  public static boolean script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = script_header(b, l + 1);
    r = r && statement_list(b, l + 1);
    exit_section_(b, m, SCRIPT, r);
    return r;
  }

  /* ********************************************************** */
  // script*
  static boolean script_file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!script(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "script_file", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LBRACKET IDENTIFIER COMMA IDENTIFIER RBRACKET parameter_list? return_list?
  public static boolean script_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, IDENTIFIER, COMMA, IDENTIFIER, RBRACKET);
    r = r && script_header_5(b, l + 1);
    r = r && script_header_6(b, l + 1);
    exit_section_(b, m, SCRIPT_HEADER, r);
    return r;
  }

  // parameter_list?
  private static boolean script_header_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header_5")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  // return_list?
  private static boolean script_header_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header_6")) return false;
    return_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // while_statement | if_statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    if (!nextTokenIs(b, "<statement>", IF, WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STATEMENT, "<statement>");
    r = while_statement(b, l + 1);
    if (!r) r = if_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // statement*
  public static boolean statement_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_list")) return false;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT_LIST, "<statement list>");
    while (true) {
      int c = current_position_(b);
      if (!statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "statement_list", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // WHILE LPAREN expression RPAREN braced_block_statement
  public static boolean while_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_statement")) return false;
    if (!nextTokenIs(b, "<statement>", WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHILE_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, WHILE, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && braced_block_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
