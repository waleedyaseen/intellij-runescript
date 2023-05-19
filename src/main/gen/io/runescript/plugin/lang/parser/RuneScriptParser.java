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
    create_token_set_(BOOLEAN_LITERAL_EXPRESSION, COMMAND_EXPRESSION, DYNAMIC_EXPRESSION, EXPRESSION,
      GOSUB_EXPRESSION, INTEGER_LITERAL_EXPRESSION, LOCAL_VARIABLE_EXPRESSION, NULL_LITERAL_EXPRESSION,
      PAR_EXPRESSION),
    create_token_set_(ARRAY_VARIABLE_ASSIGNMENT_STATEMENT, ARRAY_VARIABLE_DECLARATION_STATEMENT, BLOCK_STATEMENT, EXPRESSION_STATEMENT,
      IF_STATEMENT, LOCAL_VARIABLE_ASSIGNMENT_STATEMENT, LOCAL_VARIABLE_DECLARATION_STATEMENT, RETURN_STATEMENT,
      STATEMENT, SWITCH_STATEMENT, WHILE_STATEMENT),
  };

  /* ********************************************************** */
  // local_variable_expression par_expression EQUAL expression SEMICOLON
  public static boolean array_variable_assignment_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_variable_assignment_statement")) return false;
    if (!nextTokenIs(b, "<statement>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_VARIABLE_ASSIGNMENT_STATEMENT, "<statement>");
    r = local_variable_expression(b, l + 1);
    r = r && par_expression(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFINE_TYPE local_variable_expression par_expression SEMICOLON
  public static boolean array_variable_declaration_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_variable_declaration_statement")) return false;
    if (!nextTokenIs(b, "<statement>", DEFINE_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_VARIABLE_DECLARATION_STATEMENT, "<statement>");
    r = consumeToken(b, DEFINE_TYPE);
    r = r && local_variable_expression(b, l + 1);
    r = r && par_expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LBRACE statement_list RBRACE
  public static boolean block_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "block_statement")) return false;
    if (!nextTokenIs(b, "<statement>", LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_STATEMENT, "<statement>");
    r = consumeToken(b, LBRACE);
    r = r && statement_list(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean boolean_literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "boolean_literal_expression")) return false;
    if (!nextTokenIs(b, "<expression>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_LITERAL_EXPRESSION, "<expression>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // escaped_name (LPAREN expression_list? RPAREN)?
  public static boolean command_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, COMMAND_EXPRESSION, "<expression>");
    r = escaped_name(b, l + 1);
    r = r && command_expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LPAREN expression_list? RPAREN)?
  private static boolean command_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_1")) return false;
    command_expression_1_0(b, l + 1);
    return true;
  }

  // LPAREN expression_list? RPAREN
  private static boolean command_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && command_expression_1_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression_list?
  private static boolean command_expression_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression_1_0_1")) return false;
    expression_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // escaped_name
  public static boolean dynamic_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_EXPRESSION, "<expression>");
    r = escaped_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | DEFINE_TYPE | WHILE | IF | TRUE | FALSE | NULL | SWITCH | CASE
  static boolean escaped_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "escaped_name")) return false;
    boolean r;
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, DEFINE_TYPE);
    if (!r) r = consumeToken(b, WHILE);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, SWITCH);
    if (!r) r = consumeToken(b, CASE);
    return r;
  }

  /* ********************************************************** */
  // par_expression
  //              | local_variable_expression
  //              | literal_expression
  //              | command_expression
  //              | gosub_expression
  //              | dynamic_expression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<expression>");
    r = par_expression(b, l + 1);
    if (!r) r = local_variable_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    if (!r) r = command_expression(b, l + 1);
    if (!r) r = gosub_expression(b, l + 1);
    if (!r) r = dynamic_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // expression (COMMA expression)*
  static boolean expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
    r = r && expression_list_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression)*
  private static boolean expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_list_1", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean expression_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression SEMICOLON
  public static boolean expression_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_STATEMENT, "<statement>");
    r = expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TILDE escaped_name (LPAREN expression_list? RPAREN)?
  public static boolean gosub_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gosub_expression")) return false;
    if (!nextTokenIs(b, "<expression>", TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GOSUB_EXPRESSION, "<expression>");
    r = consumeToken(b, TILDE);
    r = r && escaped_name(b, l + 1);
    r = r && gosub_expression_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LPAREN expression_list? RPAREN)?
  private static boolean gosub_expression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gosub_expression_2")) return false;
    gosub_expression_2_0(b, l + 1);
    return true;
  }

  // LPAREN expression_list? RPAREN
  private static boolean gosub_expression_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gosub_expression_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && gosub_expression_2_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression_list?
  private static boolean gosub_expression_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gosub_expression_2_0_1")) return false;
    expression_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IF LPAREN expression RPAREN statement
  public static boolean if_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_statement")) return false;
    if (!nextTokenIs(b, "<statement>", IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // INTEGER
  public static boolean integer_literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "integer_literal_expression")) return false;
    if (!nextTokenIs(b, "<expression>", INTEGER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTEGER_LITERAL_EXPRESSION, "<expression>");
    r = consumeToken(b, INTEGER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // integer_literal_expression
  //                              | boolean_literal_expression
  //                              | null_literal_expression
  static boolean literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = integer_literal_expression(b, l + 1);
    if (!r) r = boolean_literal_expression(b, l + 1);
    if (!r) r = null_literal_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // local_variable_expression EQUAL expression SEMICOLON
  public static boolean local_variable_assignment_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_assignment_statement")) return false;
    if (!nextTokenIs(b, "<statement>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_ASSIGNMENT_STATEMENT, "<statement>");
    r = local_variable_expression(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFINE_TYPE local_variable_expression (EQUAL expression)? SEMICOLON
  public static boolean local_variable_declaration_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_declaration_statement")) return false;
    if (!nextTokenIs(b, "<statement>", DEFINE_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_DECLARATION_STATEMENT, "<statement>");
    r = consumeToken(b, DEFINE_TYPE);
    r = r && local_variable_expression(b, l + 1);
    r = r && local_variable_declaration_statement_2(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (EQUAL expression)?
  private static boolean local_variable_declaration_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_declaration_statement_2")) return false;
    local_variable_declaration_statement_2_0(b, l + 1);
    return true;
  }

  // EQUAL expression
  private static boolean local_variable_declaration_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_declaration_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DOLLAR escaped_name
  public static boolean local_variable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_EXPRESSION, "<expression>");
    r = consumeToken(b, DOLLAR);
    r = r && escaped_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NULL
  public static boolean null_literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "null_literal_expression")) return false;
    if (!nextTokenIs(b, "<expression>", NULL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NULL_LITERAL_EXPRESSION, "<expression>");
    r = consumeToken(b, NULL);
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
  // TYPE_NAME DOLLAR escaped_name
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, TYPE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TYPE_NAME, DOLLAR);
    r = r && escaped_name(b, l + 1);
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
  // RETURN LPAREN expression_list? RPAREN SEMICOLON
  public static boolean return_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement")) return false;
    if (!nextTokenIs(b, "<statement>", RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RETURN_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, RETURN, LPAREN);
    r = r && return_statement_2(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression_list?
  private static boolean return_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_2")) return false;
    expression_list(b, l + 1);
    return true;
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
  // LBRACKET escaped_name COMMA escaped_name RBRACKET parameter_list? return_list?
  public static boolean script_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && escaped_name(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && escaped_name(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
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
  // block_statement
  //             | if_statement
  //             | while_statement
  //             | switch_statement
  //             | return_statement
  //             | local_variable_declaration_statement
  //             | local_variable_assignment_statement
  //             | array_variable_declaration_statement
  //             | array_variable_assignment_statement
  //             | expression_statement
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STATEMENT, "<statement>");
    r = block_statement(b, l + 1);
    if (!r) r = if_statement(b, l + 1);
    if (!r) r = while_statement(b, l + 1);
    if (!r) r = switch_statement(b, l + 1);
    if (!r) r = return_statement(b, l + 1);
    if (!r) r = local_variable_declaration_statement(b, l + 1);
    if (!r) r = local_variable_assignment_statement(b, l + 1);
    if (!r) r = array_variable_declaration_statement(b, l + 1);
    if (!r) r = array_variable_assignment_statement(b, l + 1);
    if (!r) r = expression_statement(b, l + 1);
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
  // CASE expression_list COLON statement_list
  public static boolean switch_case(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && expression_list(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && statement_list(b, l + 1);
    exit_section_(b, m, SWITCH_CASE, r);
    return r;
  }

  /* ********************************************************** */
  // SWITCH LPAREN expression RPAREN LBRACE switch_case* RBRACE
  public static boolean switch_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_statement")) return false;
    if (!nextTokenIs(b, "<statement>", SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, SWITCH, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, LBRACE);
    r = r && switch_statement_5(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // switch_case*
  private static boolean switch_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!switch_case(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "switch_statement_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // WHILE LPAREN expression RPAREN statement
  public static boolean while_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_statement")) return false;
    if (!nextTokenIs(b, "<statement>", WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHILE_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, WHILE, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
