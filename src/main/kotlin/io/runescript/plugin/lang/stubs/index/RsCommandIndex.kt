package io.runescript.plugin.lang.stubs.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.lang.psi.op.RsOpCommand
import io.runescript.plugin.lang.stubs.types.RsOpFileStubType

class RsCommandIndex : StringStubIndexExtension<RsOpCommand>() {

    override fun getVersion(): Int = RsOpFileStubType.stubVersion
    override fun getKey(): StubIndexKey<String, RsOpCommand> = KEY

    companion object {
        val KEY = StubIndexKey.createIndexKey<String, RsOpCommand>("io.runescript.plugin.lang.stubs.index.RsCommandIndex")
    }
}
