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
    b = adapt_builder_(t, b, this, null);
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
  // script_header
  public static boolean script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = script_header(b, l + 1);
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
  // LBRACE IDENTIFIER COMMA IDENTIFIER RBRACE parameter_list? return_list?
  public static boolean script_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_header")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACE, IDENTIFIER, COMMA, IDENTIFIER, RBRACE);
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

}
