package io.runescript.plugin.symbollang.psi.stub

import com.intellij.psi.stubs.PsiFileStubImpl
import io.runescript.plugin.symbollang.psi.RsSymFile

class RsSymFileStub(file: RsSymFile?) : PsiFileStubImpl<RsSymFile>(file)