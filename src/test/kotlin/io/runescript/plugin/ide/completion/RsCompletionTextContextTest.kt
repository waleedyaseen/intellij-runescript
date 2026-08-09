package io.runescript.plugin.ide.completion

import junit.framework.TestCase

class RsCompletionTextContextTest : TestCase() {
    fun testFindsCurrentProcArgument() {
        val call = currentCall("~target(1, value", 16)

        assertEquals("target", call?.name)
        assertTrue(call?.isProc == true)
        assertEquals(1, call?.argumentIndex)
    }

    fun testIgnoresCompletedCall() {
        assertNull(currentCall("target(1)", 9))
    }

    fun testExtractsRuneScriptWordPrefix() {
        assertEquals("foo.bar:baz_1", currentWordPrefix("call foo.bar:baz_1", 18))
    }
}
