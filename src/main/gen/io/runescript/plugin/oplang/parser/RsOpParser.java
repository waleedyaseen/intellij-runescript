// This class is automatically generated. Do not edit.
package io.runescript.plugin.oplang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.runescript.plugin.oplang.psi.RsOpElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RsOpParser implements PsiParser, LightPsiParser {

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
    return File(b, l + 1);
  }

  /* ********************************************************** */
  // '#' '[' NameLiteral ('(' (AttributeValue (',' AttributeValue)*)? ')')? ']'
  public static boolean Attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute")) return false;
    if (!nextTokenIs(b, HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, HASH, LBRACKET);
    r = r && NameLiteral(b, l + 1);
    r = r && Attribute_3(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, ATTRIBUTE, r);
    return r;
  }

  // ('(' (AttributeValue (',' AttributeValue)*)? ')')?
  private static boolean Attribute_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3")) return false;
    Attribute_3_0(b, l + 1);
    return true;
  }

  // '(' (AttributeValue (',' AttributeValue)*)? ')'
  private static boolean Attribute_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && Attribute_3_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AttributeValue (',' AttributeValue)*)?
  private static boolean Attribute_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3_0_1")) return false;
    Attribute_3_0_1_0(b, l + 1);
    return true;
  }

  // AttributeValue (',' AttributeValue)*
  private static boolean Attribute_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = AttributeValue(b, l + 1);
    r = r && Attribute_3_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' AttributeValue)*
  private static boolean Attribute_3_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Attribute_3_0_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Attribute_3_0_1_0_1", c)) break;
    }
    return true;
  }

  // ',' AttributeValue
  private static boolean Attribute_3_0_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Attribute_3_0_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AttributeValue(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Attribute*
  public static boolean AttributeList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeList")) return false;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_LIST, "<attribute list>");
    while (true) {
      int c = current_position_(b);
      if (!Attribute(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AttributeList", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // NameLiteral | IntegerValue
  public static boolean AttributeValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AttributeValue")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ATTRIBUTE_VALUE, "<attribute value>");
    r = NameLiteral(b, l + 1);
    if (!r) r = IntegerValue(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AttributeList CommandHeader ParameterList ReturnList
  public static boolean Command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Command")) return false;
    if (!nextTokenIs(b, "<command>", HASH, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND, "<command>");
    r = AttributeList(b, l + 1);
    r = r && CommandHeader(b, l + 1);
    r = r && ParameterList(b, l + 1);
    r = r && ReturnList(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' NameLiteral ',' NameLiteral ']'
  public static boolean CommandHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommandHeader")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && NameLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && NameLiteral(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, COMMAND_HEADER, r);
    return r;
  }

  /* ********************************************************** */
  // Command*
  static boolean File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Command(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "File", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // INTEGER
  public static boolean IntegerValue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntegerValue")) return false;
    if (!nextTokenIs(b, INTEGER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INTEGER);
    exit_section_(b, m, INTEGER_VALUE, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | TYPE_LITERAL
  public static boolean NameLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameLiteral")) return false;
    if (!nextTokenIs(b, "<name literal>", IDENTIFIER, TYPE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_LITERAL, "<name literal>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, TYPE_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TypeName '$' NameLiteral
  public static boolean Parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameter")) return false;
    if (!nextTokenIs(b, TYPE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeName(b, l + 1);
    r = r && consumeToken(b, DOLLAR);
    r = r && NameLiteral(b, l + 1);
    exit_section_(b, m, PARAMETER, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (Parameter (',' Parameter)*)? ')'
  public static boolean ParameterList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ParameterList_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, PARAMETER_LIST, r);
    return r;
  }

  // (Parameter (',' Parameter)*)?
  private static boolean ParameterList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1")) return false;
    ParameterList_1_0(b, l + 1);
    return true;
  }

  // Parameter (',' Parameter)*
  private static boolean ParameterList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Parameter(b, l + 1);
    r = r && ParameterList_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' Parameter)*
  private static boolean ParameterList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ParameterList_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ParameterList_1_0_1", c)) break;
    }
    return true;
  }

  // ',' Parameter
  private static boolean ParameterList_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParameterList_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (TypeName (',' TypeName)*)? ')'
  public static boolean ReturnList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnList")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ReturnList_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, RETURN_LIST, r);
    return r;
  }

  // (TypeName (',' TypeName)*)?
  private static boolean ReturnList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnList_1")) return false;
    ReturnList_1_0(b, l + 1);
    return true;
  }

  // TypeName (',' TypeName)*
  private static boolean ReturnList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = TypeName(b, l + 1);
    r = r && ReturnList_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' TypeName)*
  private static boolean ReturnList_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnList_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ReturnList_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ReturnList_1_0_1", c)) break;
    }
    return true;
  }

  // ',' TypeName
  private static boolean ReturnList_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnList_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && TypeName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TYPE_LITERAL
  public static boolean TypeName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TypeName")) return false;
    if (!nextTokenIs(b, TYPE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TYPE_LITERAL);
    exit_section_(b, m, TYPE_NAME, r);
    return r;
  }

}
