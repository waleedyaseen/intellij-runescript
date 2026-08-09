package io.runescript.plugin.lang.stubs

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubTreeBuilder
import com.intellij.util.indexing.FileContentImpl
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsFile

class RsStubTreeTest : RsParserTestCase() {
    fun testScriptBodyExpressionsAreExcludedFromStubTree() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main](int ${"$"}parameter)(int)
                {
                    def_int ${"$"}local = ${"$"}parameter;
                    %player = ${"$"}local;
                }
                """.trimIndent(),
            ) as RsFile

        val content = FileContentImpl.createByFile(file.virtualFile, project)
        val root = checkNotNull(StubTreeBuilder.buildStubTree(content)) as StubElement<*>

        @Suppress("DEPRECATION")
        val stubTypes =
            root
                .descendants()
                .drop(1)
                .map { it.stubType.toString() }
                .toList()

        assertEquals(1, stubTypes.count { it == "LOCAL_VARIABLE_EXPRESSION" })
        assertEquals(3, stubTypes.count { it == "NAME_LITERAL" })
        assertEquals(2, stubTypes.count { it == "TYPE_NAME" })
        assertFalse("SCOPED_VARIABLE_EXPRESSION" in stubTypes)
    }

    private fun StubElement<*>.descendants(): Sequence<StubElement<*>> =
        sequence {
            yield(this@descendants)
            for (child in childrenStubs) {
                yieldAll(child.descendants())
            }
        }
}
