// This is a generated file. Not intended for manual editing.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RuneScriptSwitchCase extends PsiElement {

  @NotNull
  List<RuneScriptExpression> getExpressionList();

  @NotNull
  RuneScriptStatementList getStatementList();

}
