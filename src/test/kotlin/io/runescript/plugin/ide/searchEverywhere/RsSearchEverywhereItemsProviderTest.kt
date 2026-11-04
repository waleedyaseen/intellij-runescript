package io.runescript.plugin.ide.searchEverywhere

import com.intellij.ide.vfs.rpcId
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.platform.searchEverywhere.SeFilterState
import com.intellij.platform.searchEverywhere.SeItem
import com.intellij.platform.searchEverywhere.SeItemsPreviewProvider
import com.intellij.platform.searchEverywhere.SeItemsProvider
import com.intellij.platform.searchEverywhere.SeParams
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.runescript.plugin.lang.psi.RsScript
import kotlinx.coroutines.runBlocking

class RsSearchEverywhereItemsProviderTest : BasePlatformTestCase() {
    fun testFindsAndPresentsScriptsUsingTheNewApi() {
        myFixture.configureByText(
            "library.cs2",
            """
            [command,foo]
            {
            }

            [proc,bar]
            {
            }
            """.trimIndent(),
        )

        val factory = RsSearchEverywhereItemsProviderFactory()
        val items = mutableListOf<SeItem>()
        runBlocking {
            val provider = factory.getItemsProvider(project, DataContext.EMPTY_CONTEXT)
            assertNotNull(provider)
            assertEquals(RsSearchEverywhereItemsProviderFactory.ID, provider!!.id)

            provider.collectItems(
                SeParams("bar", SeFilterState.Empty),
                object : SeItemsProvider.Collector {
                    override suspend fun put(item: SeItem): Boolean {
                        items += item
                        return true
                    }
                },
            )
        }

        assertEquals(1, items.size)
        assertInstanceOf(items.single().rawObject, RsScript::class.java)
        runBlocking {
            assertEquals("[proc,bar]", items.single().presentation().text)
        }
    }

    fun testFindsCommandsAndOtherSupportedTriggers() {
        myFixture.configureByText(
            "library.cs2",
            """
            [command,command_target]
            {
            }

            [loadnpc,npc_target]
            {
            }
            """.trimIndent(),
        )

        assertEquals("[command,command_target]", findSingleItem("command_target"))
        assertEquals("[loadnpc,npc_target]", findSingleItem("npc_target"))
    }

    fun testCapsBroadSearches() {
        myFixture.configureByText(
            "library.cs2",
            (0..100).joinToString("\n\n") { index -> "[proc,script_$index]\n{\n}" },
        )

        val items = collectItems("")

        assertSize(100, items)
    }

    fun testProvidesSourcePreviewForScripts() {
        val file =
            myFixture.configureByText(
                "library.cs2",
                """
                [proc,preview_me]
                {
                }
                """.trimIndent(),
            )
        val item = collectItems("preview_me").single()
        val script = item.rawObject as RsScript

        runBlocking {
            val provider = RsSearchEverywhereItemsProviderFactory().getItemsProvider(project, DataContext.EMPTY_CONTEXT)!!
            assertInstanceOf(provider, SeItemsPreviewProvider::class.java)
            val preview = (provider as SeItemsPreviewProvider).getPreviewInfo(item, project)

            assertNotNull(preview)
            assertEquals(file.virtualFile.rpcId(), preview!!.fileUrl)
            assertEquals(listOf(script.textRange.startOffset to script.textRange.endOffset), preview.navigationRanges)
        }
    }

    private fun findSingleItem(query: String): String {
        val items = collectItems(query)
        assertSize(1, items)
        return runBlocking { items.single().presentation().text }
    }

    private fun collectItems(query: String): List<SeItem> {
        val items = mutableListOf<SeItem>()
        runBlocking {
            val provider = RsSearchEverywhereItemsProviderFactory().getItemsProvider(project, DataContext.EMPTY_CONTEXT)!!
            provider.collectItems(
                SeParams(query, SeFilterState.Empty),
                object : SeItemsProvider.Collector {
                    override suspend fun put(item: SeItem): Boolean {
                        items += item
                        return true
                    }
                },
            )
        }
        return items
    }
}
