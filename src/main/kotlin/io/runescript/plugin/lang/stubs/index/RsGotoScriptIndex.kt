package io.runescript.plugin.lang.stubs.index

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.RsScriptHeader
import io.runescript.plugin.lang.stubs.types.RsFileStubType


class RsGotoScriptIndex : StringStubIndexExtension<RsScript>() {

    override fun getVersion(): Int = RsFileStubType.stubVersion

    override fun getKey(): StubIndexKey<String, RsScript> = KEY

    companion object {
        val KEY = StubIndexKey.createIndexKey<String, RsScript>("io.runescript.plugin.lang.stubs.index.RsGotoScriptIndex")
    }
}
