package io.runescript.plugin.ide.neptune

import junit.framework.TestCase

class NeptuneModuleDataTest : TestCase() {
    fun testLoadStateRestoresResolvedRuntimeData() {
        val data = NeptuneModuleData()
        val state =
            NeptuneModuleData.State(
                sourcePaths = listOf("src", "generated"),
                symbolPaths = listOf("symbols"),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            )

        data.loadState(state)

        assertEquals(state.sourcePaths, data.resolvedData.sourcePaths)
        assertEquals(state.symbolPaths, data.resolvedData.symbolPaths)
        assertEquals(state.dbFindReturnsCount, data.resolvedData.dbFindReturnsCount)
        assertEquals(state.ccCreateAssertNewArg, data.resolvedData.ccCreateAssertNewArg)
        assertEquals(state.prefixPostfixExpressions, data.resolvedData.prefixPostfixExpressions)
        assertEquals(state.arraysV2, data.resolvedData.arraysV2)
        assertEquals(state.simplifiedTypeCodes, data.resolvedData.simplifiedTypeCodes)
        assertTrue(data.modificationCount > 0)
    }
}
