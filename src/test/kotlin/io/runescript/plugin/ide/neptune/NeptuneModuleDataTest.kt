package io.runescript.plugin.ide.neptune

import io.runescript.plugin.lang.psi.typechecker.trigger.ClientTriggerType
import junit.framework.TestCase

class NeptuneModuleDataTest : TestCase() {
    fun testLongSupportControlsLongDeclarations() {
        val enabledData = NeptuneResolvedData(longSupport = true)
        val disabledData = NeptuneResolvedData(longSupport = false)
        val longType = enabledData.types.find("long")

        assertTrue(enabledData.types.getOptions(longType).allowDeclaration)
        assertFalse(disabledData.types.getOptions(longType).allowDeclaration)
        assertTrue(enabledData.types.getOptions(longType).allowDeclaration)
    }

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
                longSupport = true,
                foldJoinedStringConstants = true,
                stringTemplates = true,
            )

        data.loadState(state)

        assertEquals(state.sourcePaths, data.resolvedData.sourcePaths)
        assertEquals(state.symbolPaths, data.resolvedData.symbolPaths)
        assertEquals(state.dbFindReturnsCount, data.resolvedData.dbFindReturnsCount)
        assertEquals(state.ccCreateAssertNewArg, data.resolvedData.ccCreateAssertNewArg)
        assertEquals(state.prefixPostfixExpressions, data.resolvedData.prefixPostfixExpressions)
        assertEquals(state.arraysV2, data.resolvedData.arraysV2)
        assertEquals(state.simplifiedTypeCodes, data.resolvedData.simplifiedTypeCodes)
        assertEquals(state.longSupport, data.resolvedData.longSupport)
        assertEquals(state.foldJoinedStringConstants, data.resolvedData.foldJoinedStringConstants)
        assertEquals(state.stringTemplates, data.resolvedData.stringTemplates)
        assertTrue(data.modificationCount > 0)
    }

    fun testRegistersLatestNeptuneLanguageModel() {
        val data = NeptuneResolvedData()

        assertSame(ClientTriggerType.INV_CHANGED, data.triggers.find("inv_changed"))
        assertSame(ClientTriggerType.INTERPOLATE, data.triggers.find("interpolate"))
        assertTrue("cc_find_param" in data.dynamicCommandHandlers)
        assertTrue(".cc_find_param" in data.dynamicCommandHandlers)
        assertTrue("if_query_refine" in data.dynamicCommandHandlers)
        assertTrue("inv_param" in data.dynamicCommandHandlers)
    }
}
