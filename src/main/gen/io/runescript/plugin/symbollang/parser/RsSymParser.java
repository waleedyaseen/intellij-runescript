// This class is automatically generated. Do not edit.
package io.runescript.plugin.symbollang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.runescript.plugin.symbollang.psi.RsSymElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;
import static com.intellij.psi.TokenType.*;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RsSymParser implements PsiParser, LightPsiParser {

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
  // STRING
  public static boolean Field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Field")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, FIELD, r);
    return r;
  }

  /* ********************************************************** */
  // Symbol*
  static boolean File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Symbol(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "File", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // Field '\t' Field ('\t' Field)* '\n'
  public static boolean Symbol(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Symbol")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Field(b, l + 1);
    r = r && consumeToken(b, TAB);
    r = r && Field(b, l + 1);
    r = r && Symbol_3(b, l + 1);
    r = r && consumeToken(b, NEW_LINE);
    exit_section_(b, m, SYMBOL, r);
    return r;
  }

  // ('\t' Field)*
  private static boolean Symbol_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Symbol_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Symbol_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "Symbol_3", c)) break;
    }
    return true;
  }

  // '\t' Field
  private static boolean Symbol_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Symbol_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TAB);
    r = r && Field(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

}
