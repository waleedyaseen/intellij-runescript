package io.runescript.plugin.oplang.psi.stub

import com.intellij.psi.stubs.PsiFileStubImpl
import io.runescript.plugin.oplang.psi.RsOpFile

class RsOpFileStub(file: RsOpFile?) : PsiFileStubImpl<RsOpFile>(file)