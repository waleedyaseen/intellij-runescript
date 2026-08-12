package io.runescript.plugin.ide.hierarchy

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName

class RsCallHierarchyTest : RsParserTestCase() {
    fun testFindsDistinctCallersAndCallees() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,first]
                {
                    ~middle();
                    ~middle();
                }

                [proc,middle]
                ~last();

                [proc,last]
                return;
                """.trimIndent(),
            )
        val scripts = PsiTreeUtil.getChildrenOfTypeAsList(file, RsScript::class.java).associateBy { it.name }

        assertEquals(listOf("[proc,middle]"), RsCallHierarchyQueries.callees(scripts.getValue("first")).map { it.qualifiedName })
        assertEquals(listOf("[proc,last]"), RsCallHierarchyQueries.callees(scripts.getValue("middle")).map { it.qualifiedName })
        assertEquals(listOf("[proc,first]"), RsCallHierarchyQueries.callers(scripts.getValue("middle")).map { it.qualifiedName })
        assertEquals(listOf("[proc,middle]"), RsCallHierarchyQueries.callers(scripts.getValue("last")).map { it.qualifiedName })
    }

    fun testIgnoresRecursiveSelfCalls() {
        val file = myFixture.configureByText("main.cs2", "[proc,recurse]\n~recurse();")
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertEmpty(RsCallHierarchyQueries.callers(script))
        assertEmpty(RsCallHierarchyQueries.callees(script))
    }
}
