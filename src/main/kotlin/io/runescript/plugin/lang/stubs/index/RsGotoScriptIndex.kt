package io.runescript.plugin.lang.stubs.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.psi.RsScriptName
import io.runescript.plugin.lang.stubs.types.RsFileStubType


class RsGotoScriptIndex : StringStubIndexExtension<RsScriptHeader>() {

    override fun getVersion(): Int = RsFileStubType.stubVersion
    override fun getKey(): StubIndexKey<String, RsScriptHeader> = KEY

    companion object {
        val KEY = StubIndexKey.createIndexKey<String, RsScriptHeader>("io.runescript.plugin.lang.stubs.index.RuneScriptGotoScriptIndex")
    }
}
