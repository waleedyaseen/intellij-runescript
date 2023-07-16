// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.runescript.plugin.lang.psi.RsElementTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RsParser implements PsiParser, LightPsiParser {

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
    return File(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ARRAY_VARIABLE_DECLARATION_STATEMENT, ASSIGNMENT_STATEMENT, BLOCK_STATEMENT, EXPRESSION_STATEMENT,
      IF_STATEMENT, LOCAL_VARIABLE_DECLARATION_STATEMENT, RETURN_STATEMENT, STATEMENT,
      SWITCH_STATEMENT, WHILE_STATEMENT),
    create_token_set_(ARITHMETIC_EXPRESSION, ARITHMETIC_VALUE_EXPRESSION, ARRAY_ACCESS_EXPRESSION, BOOLEAN_LITERAL_EXPRESSION,
      CALC_EXPRESSION, COMMAND_EXPRESSION, CONDITION_EXPRESSION, CONSTANT_EXPRESSION,
      COORD_LITERAL_EXPRESSION, DYNAMIC_EXPRESSION, EXPRESSION, GOSUB_EXPRESSION,
      INTEGER_LITERAL_EXPRESSION, LOCAL_VARIABLE_EXPRESSION, NULL_LITERAL_EXPRESSION, PAR_EXPRESSION,
      RELATIONAL_VALUE_EXPRESSION, SCOPED_VARIABLE_EXPRESSION, STRING_INTERPOLATION_EXPRESSION, STRING_LITERAL_EXPRESSION,
      SWITCH_CASE_DEFAULT_EXPRESSION),
  };

  /* ********************************************************** */
  // '(' ExpressionList? ')'
  public static boolean ArgumentList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList")) return false;
    if (!nextTokenIs(b, LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ArgumentList_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, ARGUMENT_LIST, r);
    return r;
  }

  // ExpressionList?
  private static boolean ArgumentList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArgumentList_1")) return false;
    ExpressionList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // ArithmeticAdditiveOperator ArithmeticMultiplicativeWrapper
  public static boolean ArithmeticAdditiveExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticAdditiveExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_EXPRESSION, "<Expression>");
    r = ArithmeticAdditiveOperator(b, l + 1);
    r = r && ArithmeticMultiplicativeWrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '+' | '-'
  public static boolean ArithmeticAdditiveOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticAdditiveOperator")) return false;
    if (!nextTokenIs(b, "<arithmetic additive operator>", MINUS, PLUS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARITHMETIC_OP, "<arithmetic additive operator>");
    r = consumeToken(b, PLUS);
    if (!r) r = consumeToken(b, MINUS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ArithmeticMultiplicativeWrapper ArithmeticAdditiveExpression*
  static boolean ArithmeticAdditiveWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticAdditiveWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArithmeticMultiplicativeWrapper(b, l + 1);
    r = r && ArithmeticAdditiveWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ArithmeticAdditiveExpression*
  private static boolean ArithmeticAdditiveWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticAdditiveWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ArithmeticAdditiveExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArithmeticAdditiveWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ArithmeticBitwiseAndOperator ArithmeticAdditiveWrapper
  public static boolean ArithmeticBitwiseAndExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseAndExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_EXPRESSION, "<Expression>");
    r = ArithmeticBitwiseAndOperator(b, l + 1);
    r = r && ArithmeticAdditiveWrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '&'
  public static boolean ArithmeticBitwiseAndOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseAndOperator")) return false;
    if (!nextTokenIs(b, AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AMPERSAND);
    exit_section_(b, m, ARITHMETIC_OP, r);
    return r;
  }

  /* ********************************************************** */
  // ArithmeticAdditiveWrapper ArithmeticBitwiseAndExpression*
  static boolean ArithmeticBitwiseAndWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseAndWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArithmeticAdditiveWrapper(b, l + 1);
    r = r && ArithmeticBitwiseAndWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ArithmeticBitwiseAndExpression*
  private static boolean ArithmeticBitwiseAndWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseAndWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ArithmeticBitwiseAndExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArithmeticBitwiseAndWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ArithmeticBitwiseOrOperator ArithmeticBitwiseAndWrapper
  public static boolean ArithmeticBitwiseOrExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseOrExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", BAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_EXPRESSION, "<Expression>");
    r = ArithmeticBitwiseOrOperator(b, l + 1);
    r = r && ArithmeticBitwiseAndWrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '|'
  public static boolean ArithmeticBitwiseOrOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseOrOperator")) return false;
    if (!nextTokenIs(b, BAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BAR);
    exit_section_(b, m, ARITHMETIC_OP, r);
    return r;
  }

  /* ********************************************************** */
  // ArithmeticBitwiseAndWrapper ArithmeticBitwiseOrExpression*
  static boolean ArithmeticBitwiseOrWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseOrWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArithmeticBitwiseAndWrapper(b, l + 1);
    r = r && ArithmeticBitwiseOrWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ArithmeticBitwiseOrExpression*
  private static boolean ArithmeticBitwiseOrWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticBitwiseOrWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ArithmeticBitwiseOrExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArithmeticBitwiseOrWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ArithmeticMultiplicativeOperator ArithmeticValueExpression
  public static boolean ArithmeticMultiplicativeExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticMultiplicativeExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARITHMETIC_EXPRESSION, "<Expression>");
    r = ArithmeticMultiplicativeOperator(b, l + 1);
    r = r && ArithmeticValueExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '*' | '/' | '%'
  public static boolean ArithmeticMultiplicativeOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticMultiplicativeOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARITHMETIC_OP, "<arithmetic multiplicative operator>");
    r = consumeToken(b, STAR);
    if (!r) r = consumeToken(b, SLASH);
    if (!r) r = consumeToken(b, PERCENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ArithmeticValueExpression ArithmeticMultiplicativeExpression*
  static boolean ArithmeticMultiplicativeWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticMultiplicativeWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ArithmeticValueExpression(b, l + 1);
    r = r && ArithmeticMultiplicativeWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ArithmeticMultiplicativeExpression*
  private static boolean ArithmeticMultiplicativeWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticMultiplicativeWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ArithmeticMultiplicativeExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ArithmeticMultiplicativeWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ('(' ArithmeticBitwiseOrWrapper ')') | Expression
  public static boolean ArithmeticValueExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticValueExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, ARITHMETIC_VALUE_EXPRESSION, "<Expression>");
    r = ArithmeticValueExpression_0(b, l + 1);
    if (!r) r = Expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' ArithmeticBitwiseOrWrapper ')'
  private static boolean ArithmeticValueExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArithmeticValueExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ArithmeticBitwiseOrWrapper(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LocalVariableExpression '(' Expression ')'
  public static boolean ArrayAccessExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayAccessExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_ACCESS_EXPRESSION, "<Expression>");
    r = LocalVariableExpression(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && Expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFINE_TYPE LocalVariableExpression '(' Expression ')' ';'
  public static boolean ArrayVariableDeclarationStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ArrayVariableDeclarationStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", DEFINE_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARRAY_VARIABLE_DECLARATION_STATEMENT, "<Statement>");
    r = consumeToken(b, DEFINE_TYPE);
    r = r && LocalVariableExpression(b, l + 1);
    r = r && consumeToken(b, LPAREN);
    r = r && Expression(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ArrayAccessExpression | LocalVariableExpression | ScopedVariableExpression
  static boolean AssignableExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignableExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", DOLLAR, PERCENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<Expression>");
    r = ArrayAccessExpression(b, l + 1);
    if (!r) r = LocalVariableExpression(b, l + 1);
    if (!r) r = ScopedVariableExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // AssignableExpression (',' AssignableExpression)* '=' Expression (',' Expression)* ';'
  public static boolean AssignmentStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", DOLLAR, PERCENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_STATEMENT, "<Statement>");
    r = AssignableExpression(b, l + 1);
    r = r && AssignmentStatement_1(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && Expression(b, l + 1);
    r = r && AssignmentStatement_4(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (',' AssignableExpression)*
  private static boolean AssignmentStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AssignmentStatement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AssignmentStatement_1", c)) break;
    }
    return true;
  }

  // ',' AssignableExpression
  private static boolean AssignmentStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && AssignableExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' Expression)*
  private static boolean AssignmentStatement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!AssignmentStatement_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "AssignmentStatement_4", c)) break;
    }
    return true;
  }

  // ',' Expression
  private static boolean AssignmentStatement_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "AssignmentStatement_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '{' StatementList '}'
  public static boolean BlockStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BlockStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BLOCK_STATEMENT, "<Statement>");
    r = consumeToken(b, LBRACE);
    r = r && StatementList(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TRUE | FALSE
  public static boolean BooleanLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "BooleanLiteralExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", FALSE, TRUE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_LITERAL_EXPRESSION, "<Expression>");
    r = consumeToken(b, TRUE);
    if (!r) r = consumeToken(b, FALSE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CALC '(' ArithmeticBitwiseOrWrapper ')'
  public static boolean CalcExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CalcExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", CALC)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CALC_EXPRESSION, "<Expression>");
    r = consumeTokens(b, 0, CALC, LPAREN);
    r = r && ArithmeticBitwiseOrWrapper(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NameLiteral ArgumentList
  public static boolean CommandExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CommandExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMAND_EXPRESSION, "<Expression>");
    r = NameLiteral(b, l + 1);
    r = r && ArgumentList(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CompareOperator RelationalValueExpression
  public static boolean CompareExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CompareExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, CONDITION_EXPRESSION, "<Expression>");
    r = CompareOperator(b, l + 1);
    r = r && RelationalValueExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // RelationalValueExpression CompareExpression?
  static boolean CompareExpressionWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CompareExpressionWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = RelationalValueExpression(b, l + 1);
    r = r && CompareExpressionWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CompareExpression?
  private static boolean CompareExpressionWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CompareExpressionWrapper_1")) return false;
    CompareExpression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '!' | '>' | '<' | '>=' | '<=' | '='
  public static boolean CompareOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CompareOperator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONDITION_OP, "<compare operator>");
    r = consumeToken(b, EXCEL);
    if (!r) r = consumeToken(b, GT);
    if (!r) r = consumeToken(b, LT);
    if (!r) r = consumeToken(b, GTE);
    if (!r) r = consumeToken(b, LTE);
    if (!r) r = consumeToken(b, EQUAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '^' NameLiteral
  public static boolean ConstantExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ConstantExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", CARET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONSTANT_EXPRESSION, "<Expression>");
    r = consumeToken(b, CARET);
    r = r && NameLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COORDGRID
  public static boolean CoordLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "CoordLiteralExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", COORDGRID)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COORD_LITERAL_EXPRESSION, "<Expression>");
    r = consumeToken(b, COORDGRID);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // NameLiteral
  public static boolean DynamicExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DynamicExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_EXPRESSION, "<Expression>");
    r = NameLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ParExpression
  //              | ArrayAccessExpression
  //              | LocalVariableExpression
  //              | ScopedVariableExpression
  //              | LiteralExpression
  //              | CommandExpression
  //              | GosubExpression
  //              | ConstantExpression
  //              | DynamicExpression
  //              | CalcExpression
  public static boolean Expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXPRESSION, "<Expression>");
    r = ParExpression(b, l + 1);
    if (!r) r = ArrayAccessExpression(b, l + 1);
    if (!r) r = LocalVariableExpression(b, l + 1);
    if (!r) r = ScopedVariableExpression(b, l + 1);
    if (!r) r = LiteralExpression(b, l + 1);
    if (!r) r = CommandExpression(b, l + 1);
    if (!r) r = GosubExpression(b, l + 1);
    if (!r) r = ConstantExpression(b, l + 1);
    if (!r) r = DynamicExpression(b, l + 1);
    if (!r) r = CalcExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Expression (',' Expression)*
  static boolean ExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = Expression(b, l + 1);
    r = r && ExpressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' Expression)*
  private static boolean ExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ExpressionList_1", c)) break;
    }
    return true;
  }

  // ',' Expression
  private static boolean ExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // Expression ';'
  public static boolean ExpressionStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ExpressionStatement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_STATEMENT, "<Statement>");
    r = Expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Script*
  static boolean File(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "File")) return false;
    while (true) {
      int c = current_position_(b);
      if (!Script(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "File", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // '~' NameLiteral ArgumentList?
  public static boolean GosubExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GosubExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", TILDE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, GOSUB_EXPRESSION, "<Expression>");
    r = consumeToken(b, TILDE);
    r = r && NameLiteral(b, l + 1);
    r = r && GosubExpression_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ArgumentList?
  private static boolean GosubExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "GosubExpression_2")) return false;
    ArgumentList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IF '(' LogicalOrWrapper ')' Statement (ELSE Statement)?
  public static boolean IfStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", IF)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_STATEMENT, "<Statement>");
    r = consumeTokens(b, 0, IF, LPAREN);
    r = r && LogicalOrWrapper(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && Statement(b, l + 1);
    r = r && IfStatement_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ELSE Statement)?
  private static boolean IfStatement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfStatement_5")) return false;
    IfStatement_5_0(b, l + 1);
    return true;
  }

  // ELSE Statement
  private static boolean IfStatement_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IfStatement_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && Statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // INTEGER
  public static boolean IntegerLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "IntegerLiteralExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", INTEGER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTEGER_LITERAL_EXPRESSION, "<Expression>");
    r = consumeToken(b, INTEGER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IntegerLiteralExpression
  //                              | CoordLiteralExpression
  //                              | BooleanLiteralExpression
  //                              | NullLiteralExpression
  //                              | StringLiteralExpression
  static boolean LiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LiteralExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<Expression>");
    r = IntegerLiteralExpression(b, l + 1);
    if (!r) r = CoordLiteralExpression(b, l + 1);
    if (!r) r = BooleanLiteralExpression(b, l + 1);
    if (!r) r = NullLiteralExpression(b, l + 1);
    if (!r) r = StringLiteralExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // DEFINE_TYPE LocalVariableExpression ('=' Expression)? ';'
  public static boolean LocalVariableDeclarationStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LocalVariableDeclarationStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", DEFINE_TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_DECLARATION_STATEMENT, "<Statement>");
    r = consumeToken(b, DEFINE_TYPE);
    r = r && LocalVariableExpression(b, l + 1);
    r = r && LocalVariableDeclarationStatement_2(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('=' Expression)?
  private static boolean LocalVariableDeclarationStatement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LocalVariableDeclarationStatement_2")) return false;
    LocalVariableDeclarationStatement_2_0(b, l + 1);
    return true;
  }

  // '=' Expression
  private static boolean LocalVariableDeclarationStatement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LocalVariableDeclarationStatement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUAL);
    r = r && Expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '$' NameLiteral
  public static boolean LocalVariableExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LocalVariableExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", DOLLAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_VARIABLE_EXPRESSION, "<Expression>");
    r = consumeToken(b, DOLLAR);
    r = r && NameLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LogicalAndOperator CompareExpressionWrapper
  public static boolean LogicalAndExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalAndExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, CONDITION_EXPRESSION, "<Expression>");
    r = LogicalAndOperator(b, l + 1);
    r = r && CompareExpressionWrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '&'
  public static boolean LogicalAndOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalAndOperator")) return false;
    if (!nextTokenIs(b, AMPERSAND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AMPERSAND);
    exit_section_(b, m, CONDITION_OP, r);
    return r;
  }

  /* ********************************************************** */
  // CompareExpressionWrapper LogicalAndExpression*
  static boolean LogicalAndWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalAndWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = CompareExpressionWrapper(b, l + 1);
    r = r && LogicalAndWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LogicalAndExpression*
  private static boolean LogicalAndWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalAndWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!LogicalAndExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LogicalAndWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LogicalOrOperator LogicalAndWrapper
  public static boolean LogicalOrExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalOrExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", BAR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, CONDITION_EXPRESSION, "<Expression>");
    r = LogicalOrOperator(b, l + 1);
    r = r && LogicalAndWrapper(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '|'
  public static boolean LogicalOrOperator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalOrOperator")) return false;
    if (!nextTokenIs(b, BAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BAR);
    exit_section_(b, m, CONDITION_OP, r);
    return r;
  }

  /* ********************************************************** */
  // LogicalAndWrapper LogicalOrExpression*
  static boolean LogicalOrWrapper(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalOrWrapper")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = LogicalAndWrapper(b, l + 1);
    r = r && LogicalOrWrapper_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LogicalOrExpression*
  private static boolean LogicalOrWrapper_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "LogicalOrWrapper_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!LogicalOrExpression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "LogicalOrWrapper_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER | DEFINE_TYPE | TYPE_LITERAL | ARRAY_TYPE_LITERAL | WHILE | IF | TRUE | FALSE | NULL | SWITCH | CASE
  public static boolean NameLiteral(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NameLiteral")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAME_LITERAL, "<name literal>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, DEFINE_TYPE);
    if (!r) r = consumeToken(b, TYPE_LITERAL);
    if (!r) r = consumeToken(b, ARRAY_TYPE_LITERAL);
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
  public static boolean NullLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "NullLiteralExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", NULL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NULL_LITERAL_EXPRESSION, "<Expression>");
    r = consumeToken(b, NULL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '(' Expression ')'
  public static boolean ParExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ParExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", LPAREN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PAR_EXPRESSION, "<Expression>");
    r = consumeToken(b, LPAREN);
    r = r && Expression(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (TypeName | ARRAY_TYPE_LITERAL) LocalVariableExpression
  public static boolean Parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameter")) return false;
    if (!nextTokenIs(b, "<parameter>", ARRAY_TYPE_LITERAL, TYPE_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = Parameter_0(b, l + 1);
    r = r && LocalVariableExpression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // TypeName | ARRAY_TYPE_LITERAL
  private static boolean Parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Parameter_0")) return false;
    boolean r;
    r = TypeName(b, l + 1);
    if (!r) r = consumeToken(b, ARRAY_TYPE_LITERAL);
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
  // ('(' LogicalOrWrapper ')') | Expression
  public static boolean RelationalValueExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RelationalValueExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, RELATIONAL_VALUE_EXPRESSION, "<Expression>");
    r = RelationalValueExpression_0(b, l + 1);
    if (!r) r = Expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '(' LogicalOrWrapper ')'
  private static boolean RelationalValueExpression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RelationalValueExpression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && LogicalOrWrapper(b, l + 1);
    r = r && consumeToken(b, RPAREN);
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
  // RETURN ('(' ExpressionList? ')')? ';'
  public static boolean ReturnStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RETURN_STATEMENT, "<Statement>");
    r = consumeToken(b, RETURN);
    r = r && ReturnStatement_1(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ('(' ExpressionList? ')')?
  private static boolean ReturnStatement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement_1")) return false;
    ReturnStatement_1_0(b, l + 1);
    return true;
  }

  // '(' ExpressionList? ')'
  private static boolean ReturnStatement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPAREN);
    r = r && ReturnStatement_1_0_1(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // ExpressionList?
  private static boolean ReturnStatement_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ReturnStatement_1_0_1")) return false;
    ExpressionList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // '%' NameLiteral
  public static boolean ScopedVariableExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ScopedVariableExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", PERCENT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCOPED_VARIABLE_EXPRESSION, "<Expression>");
    r = consumeToken(b, PERCENT);
    r = r && NameLiteral(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' NameLiteral ',' NameLiteral ']' ParameterList? ReturnList? StatementList
  public static boolean Script(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Script")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && NameLiteral(b, l + 1);
    r = r && consumeToken(b, COMMA);
    r = r && NameLiteral(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    r = r && Script_5(b, l + 1);
    r = r && Script_6(b, l + 1);
    r = r && StatementList(b, l + 1);
    exit_section_(b, m, SCRIPT, r);
    return r;
  }

  // ParameterList?
  private static boolean Script_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Script_5")) return false;
    ParameterList(b, l + 1);
    return true;
  }

  // ReturnList?
  private static boolean Script_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Script_6")) return false;
    ReturnList(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // BlockStatement
  //             | IfStatement
  //             | WhileStatement
  //             | SwitchStatement
  //             | ReturnStatement
  //             | LocalVariableDeclarationStatement
  //             | ArrayVariableDeclarationStatement
  //             | AssignmentStatement
  //             | ExpressionStatement
  public static boolean Statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "Statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, STATEMENT, "<Statement>");
    r = BlockStatement(b, l + 1);
    if (!r) r = IfStatement(b, l + 1);
    if (!r) r = WhileStatement(b, l + 1);
    if (!r) r = SwitchStatement(b, l + 1);
    if (!r) r = ReturnStatement(b, l + 1);
    if (!r) r = LocalVariableDeclarationStatement(b, l + 1);
    if (!r) r = ArrayVariableDeclarationStatement(b, l + 1);
    if (!r) r = AssignmentStatement(b, l + 1);
    if (!r) r = ExpressionStatement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // Statement*
  public static boolean StatementList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StatementList")) return false;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT_LIST, "<statement list>");
    while (true) {
      int c = current_position_(b);
      if (!Statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "StatementList", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // STRING_INTERPOLATION_START Expression STRING_INTERPOLATION_END
  public static boolean StringInterpolationExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StringInterpolationExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", STRING_INTERPOLATION_START)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_INTERPOLATION_EXPRESSION, "<Expression>");
    r = consumeToken(b, STRING_INTERPOLATION_START);
    r = r && Expression(b, l + 1);
    r = r && consumeToken(b, STRING_INTERPOLATION_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (STRING_PART | STRING_TAG | StringInterpolationExpression)*
  public static boolean StringLiteralContent(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StringLiteralContent")) return false;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL_CONTENT, "<string literal content>");
    while (true) {
      int c = current_position_(b);
      if (!StringLiteralContent_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "StringLiteralContent", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // STRING_PART | STRING_TAG | StringInterpolationExpression
  private static boolean StringLiteralContent_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StringLiteralContent_0")) return false;
    boolean r;
    r = consumeToken(b, STRING_PART);
    if (!r) r = consumeToken(b, STRING_TAG);
    if (!r) r = StringInterpolationExpression(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // STRING_START StringLiteralContent STRING_END
  public static boolean StringLiteralExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "StringLiteralExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", STRING_START)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRING_LITERAL_EXPRESSION, "<Expression>");
    r = consumeToken(b, STRING_START);
    r = r && StringLiteralContent(b, l + 1);
    r = r && consumeToken(b, STRING_END);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // CASE SwitchCaseExpressionList ':' StatementList
  public static boolean SwitchCase(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCase")) return false;
    if (!nextTokenIs(b, CASE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CASE);
    r = r && SwitchCaseExpressionList(b, l + 1);
    r = r && consumeToken(b, COLON);
    r = r && StatementList(b, l + 1);
    exit_section_(b, m, SWITCH_CASE, r);
    return r;
  }

  /* ********************************************************** */
  // DEFAULT
  public static boolean SwitchCaseDefaultExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCaseDefaultExpression")) return false;
    if (!nextTokenIs(b, "<Expression>", DEFAULT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_CASE_DEFAULT_EXPRESSION, "<Expression>");
    r = consumeToken(b, DEFAULT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SwitchCaseDefaultExpression | Expression
  static boolean SwitchCaseExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCaseExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<Expression>");
    r = SwitchCaseDefaultExpression(b, l + 1);
    if (!r) r = Expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SwitchCaseExpression (',' SwitchCaseExpression)*
  static boolean SwitchCaseExpressionList(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCaseExpressionList")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = SwitchCaseExpression(b, l + 1);
    r = r && SwitchCaseExpressionList_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' SwitchCaseExpression)*
  private static boolean SwitchCaseExpressionList_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCaseExpressionList_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!SwitchCaseExpressionList_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "SwitchCaseExpressionList_1", c)) break;
    }
    return true;
  }

  // ',' SwitchCaseExpression
  private static boolean SwitchCaseExpressionList_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchCaseExpressionList_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && SwitchCaseExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SWITCH '(' Expression ')' '{' SwitchCase* '}'
  public static boolean SwitchStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", SWITCH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_STATEMENT, "<Statement>");
    r = consumeTokens(b, 0, SWITCH, LPAREN);
    r = r && Expression(b, l + 1);
    r = r && consumeTokens(b, 0, RPAREN, LBRACE);
    r = r && SwitchStatement_5(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SwitchCase*
  private static boolean SwitchStatement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "SwitchStatement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!SwitchCase(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "SwitchStatement_5", c)) break;
    }
    return true;
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

  /* ********************************************************** */
  // WHILE '(' LogicalOrWrapper ')' Statement
  public static boolean WhileStatement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "WhileStatement")) return false;
    if (!nextTokenIs(b, "<Statement>", WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHILE_STATEMENT, "<Statement>");
    r = consumeTokens(b, 0, WHILE, LPAREN);
    r = r && LogicalOrWrapper(b, l + 1);
    r = r && consumeToken(b, RPAREN);
    r = r && Statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
