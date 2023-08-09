// This class is automatically generated. Do not edit.
package io.runescript.plugin.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import io.runescript.plugin.lang.psi.type.inference.RsInferenceDataHolder;

public interface RsHookFragment extends RsNamedElement, RsInferenceDataHolder {

  @Nullable
  RsArgumentList getArgumentList();

  @Nullable
  RsHookTransmitList getHookTransmitList();

  @NotNull
  RsNameLiteral getNameLiteral();

}
