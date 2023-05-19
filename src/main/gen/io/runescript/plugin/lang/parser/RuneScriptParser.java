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
    create_token_set_(ARRAY_VARIABLE_ASSIGNMENT_STATEMENT, ARRAY_VARIABLE_DECLARATION_STATEMENT, BLOCK_STATEMENT, EXPRESSION_STATEMENT,
      IF_STATEMENT, LOCAL_VARIABLE_ASSIGNMENT_STATEMENT, LOCAL_VARIABLE_DECLARATION_STATEMENT, RETURN_STATEMENT,
      SCOPED_VARIABLE_ASSIGNMENT_STATEMENT, STATEMENT, SWITCH_STATEMENT, WHILE_STATEMENT),
    create_token_set_(ARITHMETIC_ADDITIVE_EXPRESSION, ARITHMETIC_BITWISE_AND_EXPRESSION, ARITHMETIC_BITWISE_OR_EXPRESSION, ARITHMETIC_EXPRESSION,
      ARITHMETIC_MULTIPLICATIVE_EXPRESSION, ARITHMETIC_VALUE_EXPRESSION, ARRAY_VARIABLE_EXPRESSION, BOOLEAN_LITERAL_EXPRESSION,
      CALC_EXPRESSION, COMMAND_EXPRESSION, COMPARE_EXPRESSION, CONSTANT_EXPRESSION,
      DYNAMIC_EXPRESSION, EXPRESSION, GOSUB_EXPRESSION, INTEGER_LITERAL_EXPRESSION,
      LOCAL_VARIABLE_EXPRESSION, LOGICAL_AND_EXPRESSION, LOGICAL_OR_EXPRESSION, NULL_LITERAL_EXPRESSION,
      PAR_EXPRESSION, RELATIONAL_VALUE_EXPRESSION, SCOPED_VARIABLE_EXPRESSION, STRING_INTERPOLATION_EXPRESSION,
      STRING_LITERAL_EXPRESSION),
  };

  /* ********************************************************** */
  // (PLUS | MINUS) arithmetic_multiplicative_wrapper
  public static boolean arithmetic_additive_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_additive_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_ADDITIVE_EXPRESSION, "<expression>");
    r = arithmetic_additive_expression_0(b, l + 1);
    r = r && arithmetic_multiplicative_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // PLUS | MINUS
  private static boolean arithmetic_additive_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_additive_expression_0")) return false;
    boolean r;
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    return r;
  }

  /* ********************************************************** */
  // arithmetic_multiplicative_wrapper arithmetic_additive_expression*
  static boolean arithmetic_additive_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_additive_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arithmetic_multiplicative_wrapper(b, l + 1);
    r = r && arithmetic_additive_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // arithmetic_additive_expression*
  private static boolean arithmetic_additive_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_additive_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arithmetic_additive_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arithmetic_additive_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // AMPERSAND arithmetic_additive_wrapper
  public static boolean arithmetic_bitwise_and_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_and_expression")) return false;
    if (!nextTokenIs(b, "<expression>", AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_BITWISE_AND_EXPRESSION, "<expression>");
    r = consumeToken(b, AMPERSAND);
    r = r && arithmetic_additive_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // arithmetic_additive_wrapper arithmetic_bitwise_and_expression*
  static boolean arithmetic_bitwise_and_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_and_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arithmetic_additive_wrapper(b, l + 1);
    r = r && arithmetic_bitwise_and_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // arithmetic_bitwise_and_expression*
  private static boolean arithmetic_bitwise_and_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_and_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arithmetic_bitwise_and_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arithmetic_bitwise_and_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // BAR arithmetic_bitwise_and_wrapper
  public static boolean arithmetic_bitwise_or_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_or_expression")) return false;
    if (!nextTokenIs(b, "<expression>", BAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_BITWISE_OR_EXPRESSION, "<expression>");
    r = consumeToken(b, BAR);
    r = r && arithmetic_bitwise_and_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // arithmetic_bitwise_and_wrapper arithmetic_bitwise_or_expression*
  static boolean arithmetic_bitwise_or_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_or_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arithmetic_bitwise_and_wrapper(b, l + 1);
    r = r && arithmetic_bitwise_or_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // arithmetic_bitwise_or_expression*
  private static boolean arithmetic_bitwise_or_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_bitwise_or_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arithmetic_bitwise_or_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arithmetic_bitwise_or_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // arithmetic_bitwise_or_wrapper
  public static boolean arithmetic_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ARITHMETIC_EXPRESSION, "<expression>");
    r = arithmetic_bitwise_or_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (STAR | SLASH | PERCENT) arithmetic_value_expression
  public static boolean arithmetic_multiplicative_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_multiplicative_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_MULTIPLICATIVE_EXPRESSION, "<expression>");
    r = arithmetic_multiplicative_expression_0(b, l + 1);
    r = r && arithmetic_value_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // STAR | SLASH | PERCENT
  private static boolean arithmetic_multiplicative_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_multiplicative_expression_0")) return false;
    boolean r;
    r = consumeToken(b, STAR);
    if (!r) r = consumeToken(b, SLASH);
    if (!r) r = consumeToken(b, PERCENT);
    return r;
  }

  /* ********************************************************** */
  // arithmetic_value_expression arithmetic_multiplicative_expression*
  static boolean arithmetic_multiplicative_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_multiplicative_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = arithmetic_value_expression(b, l + 1);
    r = r && arithmetic_multiplicative_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // arithmetic_multiplicative_expression*
  private static boolean arithmetic_multiplicative_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_multiplicative_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!arithmetic_multiplicative_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "arithmetic_multiplicative_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (LPAREN arithmetic_expression RPAREN) | expression
  public static boolean arithmetic_value_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_value_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ARITHMETIC_VALUE_EXPRESSION, "<expression>");
    r = arithmetic_value_expression_0(b, l + 1);
    if (!r) r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LPAREN arithmetic_expression RPAREN
  private static boolean arithmetic_value_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "arithmetic_value_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && arithmetic_expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

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
  // DOLLAR name_literal LPAREN expression RPAREN
  public static boolean array_variable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_variable_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_VARIABLE_EXPRESSION, "<expression>");
    r = consumeToken(b, DOLLAR);
    r = r && name_literal(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
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
  // CALC LPAREN arithmetic_expression RPAREN
  public static boolean calc_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "calc_expression")) return false;
    if (!nextTokenIs(b, "<expression>", CALC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CALC_EXPRESSION, "<expression>");
    r = consumeTokens(b, 0, CALC, LPAREN);
    r = r && arithmetic_expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // name_literal (LPAREN expression_list? RPAREN)?
  public static boolean command_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_EXPRESSION, "<expression>");
    r = name_literal(b, l + 1);
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
  // (EXCEL | GT | LT | GTE | LTE | EQUAL) relational_value_expression
  public static boolean compare_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compare_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, COMPARE_EXPRESSION, "<expression>");
    r = compare_expression_0(b, l + 1);
    r = r && relational_value_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EXCEL | GT | LT | GTE | LTE | EQUAL
  private static boolean compare_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compare_expression_0")) return false;
    boolean r;
    r = consumeToken(b, EXCEL);
    if (!r) r = consumeToken(b, GT);
    if (!r) r = consumeToken(b, LT);
    if (!r) r = consumeToken(b, GTE);
    if (!r) r = consumeToken(b, LTE);
    if (!r) r = consumeToken(b, EQUAL);
    return r;
  }

  /* ********************************************************** */
  // relational_value_expression compare_expression?
  static boolean compare_expression_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compare_expression_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = relational_value_expression(b, l + 1);
    r = r && compare_expression_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // compare_expression?
  private static boolean compare_expression_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compare_expression_wrapper_1")) return false;
    compare_expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // CARET name_literal
  public static boolean constant_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant_expression")) return false;
    if (!nextTokenIs(b, "<expression>", CARET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONSTANT_EXPRESSION, "<expression>");
    r = consumeToken(b, CARET);
    r = r && name_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // name_literal
  public static boolean dynamic_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_EXPRESSION, "<expression>");
    r = name_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // par_expression
  //              | array_variable_expression
  //              | local_variable_expression
  //              | scoped_variable_expression
  //              | literal_expression
  //              | command_expression
  //              | gosub_expression
  //              | constant_expression
  //              | dynamic_expression
  //              | calc_expression
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<expression>");
    r = par_expression(b, l + 1);
    if (!r) r = array_variable_expression(b, l + 1);
    if (!r) r = local_variable_expression(b, l + 1);
    if (!r) r = scoped_variable_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    if (!r) r = command_expression(b, l + 1);
    if (!r) r = gosub_expression(b, l + 1);
    if (!r) r = constant_expression(b, l + 1);
    if (!r) r = dynamic_expression(b, l + 1);
    if (!r) r = calc_expression(b, l + 1);
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
  // TILDE name_literal (LPAREN expression_list? RPAREN)?
  public static boolean gosub_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "gosub_expression")) return false;
    if (!nextTokenIs(b, "<expression>", TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GOSUB_EXPRESSION, "<expression>");
    r = consumeToken(b, TILDE);
    r = r && name_literal(b, l + 1);
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
  // IF LPAREN relational_expression RPAREN statement
  public static boolean if_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_statement")) return false;
    if (!nextTokenIs(b, "<statement>", IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && relational_expression(b, l + 1);
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
  //                              | string_literal_expression
  static boolean literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = integer_literal_expression(b, l + 1);
    if (!r) r = boolean_literal_expression(b, l + 1);
    if (!r) r = null_literal_expression(b, l + 1);
    if (!r) r = string_literal_expression(b, l + 1);
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
  // DOLLAR name_literal
  public static boolean local_variable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_variable_expression")) return false;
    if (!nextTokenIs(b, "<expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_EXPRESSION, "<expression>");
    r = consumeToken(b, DOLLAR);
    r = r && name_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AMPERSAND compare_expression_wrapper
  public static boolean logical_and_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_and_expression")) return false;
    if (!nextTokenIs(b, "<expression>", AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, LOGICAL_AND_EXPRESSION, "<expression>");
    r = consumeToken(b, AMPERSAND);
    r = r && compare_expression_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // compare_expression_wrapper logical_and_expression*
  static boolean logical_and_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_and_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = compare_expression_wrapper(b, l + 1);
    r = r && logical_and_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // logical_and_expression*
  private static boolean logical_and_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_and_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!logical_and_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "logical_and_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // BAR logical_and_wrapper
  public static boolean logical_or_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_or_expression")) return false;
    if (!nextTokenIs(b, "<expression>", BAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, LOGICAL_OR_EXPRESSION, "<expression>");
    r = consumeToken(b, BAR);
    r = r && logical_and_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // logical_and_wrapper logical_or_expression*
  static boolean logical_or_wrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_or_wrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = logical_and_wrapper(b, l + 1);
    r = r && logical_or_wrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // logical_or_expression*
  private static boolean logical_or_wrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logical_or_wrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!logical_or_expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "logical_or_wrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER | DEFINE_TYPE | ARRAY_TYPE_NAME | WHILE | IF | TRUE | FALSE | NULL | SWITCH | CASE
  public static boolean name_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_LITERAL, "<name literal>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, DEFINE_TYPE);
    if (!r) r = consumeToken(b, ARRAY_TYPE_NAME);
    if (!r) r = consumeToken(b, WHILE);
    if (!r) r = consumeToken(b, IF);
    if (!r) r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    if (!r) r = consumeToken(b, NULL);
    if (!r) r = consumeToken(b, SWITCH);
    if (!r) r = consumeToken(b, CASE);
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
  // (TYPE_NAME | ARRAY_TYPE_NAME) DOLLAR name_literal
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, "<parameter>", ARRAY_TYPE_NAME, TYPE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = parameter_0(b, l + 1);
    r = r && consumeToken(b, DOLLAR);
    r = r && name_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // TYPE_NAME | ARRAY_TYPE_NAME
  private static boolean parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0")) return false;
    boolean r;
    r = consumeToken(b, TYPE_NAME);
    if (!r) r = consumeToken(b, ARRAY_TYPE_NAME);
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
  // logical_or_wrapper
  static boolean relational_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = logical_or_wrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (LPAREN relational_expression RPAREN) | expression
  public static boolean relational_value_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_value_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, RELATIONAL_VALUE_EXPRESSION, "<expression>");
    r = relational_value_expression_0(b, l + 1);
    if (!r) r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LPAREN relational_expression RPAREN
  private static boolean relational_value_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "relational_value_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && relational_expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
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
  // RETURN (LPAREN expression_list? RPAREN)? SEMICOLON
  public static boolean return_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement")) return false;
    if (!nextTokenIs(b, "<statement>", RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RETURN_STATEMENT, "<statement>");
    r = consumeToken(b, RETURN);
    r = r && return_statement_1(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LPAREN expression_list? RPAREN)?
  private static boolean return_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1")) return false;
    return_statement_1_0(b, l + 1);
    return true;
  }

  // LPAREN expression_list? RPAREN
  private static boolean return_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && return_statement_1_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression_list?
  private static boolean return_statement_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0_1")) return false;
    expression_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // scoped_variable_expression EQUAL expression SEMICOLON
  public static boolean scoped_variable_assignment_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scoped_variable_assignment_statement")) return false;
    if (!nextTokenIs(b, "<statement>", MOD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCOPED_VARIABLE_ASSIGNMENT_STATEMENT, "<statement>");
    r = scoped_variable_expression(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // MOD name_literal
  public static boolean scoped_variable_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scoped_variable_expression")) return false;
    if (!nextTokenIs(b, "<expression>", MOD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCOPED_VARIABLE_EXPRESSION, "<expression>");
    r = consumeToken(b, MOD);
    r = r && name_literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // script_name parameter_list? return_list?
  public static boolean script_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = script_name(b, l + 1);
    r = r && script_header_1(b, l + 1);
    r = r && script_header_2(b, l + 1);
    exit_section_(b, m, SCRIPT_HEADER, r);
    return r;
  }

  // parameter_list?
  private static boolean script_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header_1")) return false;
    parameter_list(b, l + 1);
    return true;
  }

  // return_list?
  private static boolean script_header_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header_2")) return false;
    return_list(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBRACKET name_literal COMMA name_literal RBRACKET
  public static boolean script_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_name")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && name_literal(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && name_literal(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, SCRIPT_NAME, r);
    return r;
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
  //             | scoped_variable_assignment_statement
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
    if (!r) r = scoped_variable_assignment_statement(b, l + 1);
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
  // STRING_INTERPOLATION_START | expression | STRING_INTERPOLATION_END
  public static boolean string_interpolation_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_interpolation_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STRING_INTERPOLATION_EXPRESSION, "<expression>");
    r = consumeToken(b, STRING_INTERPOLATION_START);
    if (!r) r = expression(b, l + 1);
    if (!r) r = consumeToken(b, STRING_INTERPOLATION_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // STRING_START (STRING_PART | STRING_TAG | string_interpolation_expression)* STRING_END
  public static boolean string_literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal_expression")) return false;
    if (!nextTokenIs(b, "<expression>", STRING_START)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL_EXPRESSION, "<expression>");
    r = consumeToken(b, STRING_START);
    r = r && string_literal_expression_1(b, l + 1);
    r = r && consumeToken(b, STRING_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (STRING_PART | STRING_TAG | string_interpolation_expression)*
  private static boolean string_literal_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!string_literal_expression_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "string_literal_expression_1", c)) break;
    }
    return true;
  }

  // STRING_PART | STRING_TAG | string_interpolation_expression
  private static boolean string_literal_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "string_literal_expression_1_0")) return false;
    boolean r;
    r = consumeToken(b, STRING_PART);
    if (!r) r = consumeToken(b, STRING_TAG);
    if (!r) r = string_interpolation_expression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CASE switch_case_expression_list COLON statement_list
  public static boolean switch_case(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && switch_case_expression_list(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && statement_list(b, l + 1);
    exit_section_(b, m, SWITCH_CASE, r);
    return r;
  }

  /* ********************************************************** */
  // expression | DEFAULT
  static boolean switch_case_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<expression>");
    r = expression(b, l + 1);
    if (!r) r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // switch_case_expression (COMMA switch_case_expression)*
  static boolean switch_case_expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case_expression_list")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = switch_case_expression(b, l + 1);
    r = r && switch_case_expression_list_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA switch_case_expression)*
  private static boolean switch_case_expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case_expression_list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!switch_case_expression_list_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "switch_case_expression_list_1", c)) break;
    }
    return true;
  }

  // COMMA switch_case_expression
  private static boolean switch_case_expression_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case_expression_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && switch_case_expression(b, l + 1);
    exit_section_(b, m, null, r);
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
  // WHILE LPAREN relational_expression RPAREN statement
  public static boolean while_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_statement")) return false;
    if (!nextTokenIs(b, "<statement>", WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHILE_STATEMENT, "<statement>");
    r = consumeTokens(b, 0, WHILE, LPAREN);
    r = r && relational_expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
