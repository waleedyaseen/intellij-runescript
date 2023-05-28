package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.PsiFileStubImpl
import io.runescript.plugin.lang.psi.RsOpFile

class RsOpFileStub(file: RsOpFile?) : PsiFileStubImpl<RsOpFile>(file)