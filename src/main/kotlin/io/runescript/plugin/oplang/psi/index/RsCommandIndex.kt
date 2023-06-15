package io.runescript.plugin.oplang.psi.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.oplang.psi.RsOpCommand
import io.runescript.plugin.oplang.psi.stub.type.RsOpFileStubType

class RsCommandIndex : StringStubIndexExtension<RsOpCommand>() {

    override fun getVersion(): Int = RsOpFileStubType.stubVersion
    override fun getKey(): StubIndexKey<String, RsOpCommand> = KEY

    companion object {
        val KEY = StubIndexKey.createIndexKey<String, RsOpCommand>("io.runescript.plugin.lang.stubs.index.RsCommandIndex")
    }
}