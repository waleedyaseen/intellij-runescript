package io.runescript.plugin.ide.navigation

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.neptune.NeptuneProjectImportData
import io.runescript.plugin.ide.neptune.neptuneModuleData
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.symbollang.psi.RsSymSymbol

class RsQualifiedNameProviderTest : RsParserTestCase() {
    private val provider = RsQualifiedNameProvider()

    override fun setUp() {
        super.setUp()
        module.neptuneModuleData.updateFromImportData(
            NeptuneProjectImportData(
                name = "test",
                sourcePaths = emptyList(),
                symbolPaths = listOf("symbols"),
                dbFindReturnsCount = true,
                ccCreateAssertNewArg = true,
                prefixPostfixExpressions = true,
                arraysV2 = true,
                simplifiedTypeCodes = true,
            ),
        )
    }

    fun testCopiesAndResolvesScriptQualifiedName() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,target]
                return;

                [proc,caller]
                ~target();
                """.trimIndent(),
            )
        val scripts = PsiTreeUtil.getChildrenOfTypeAsList(file, RsScript::class.java)
        val target = scripts.first()
        val call = PsiTreeUtil.findChildOfType(scripts.last(), RsGosubExpression::class.java)!!

        assertEquals("[proc,target]", provider.getQualifiedName(target))
        assertEquals(target, provider.adjustElementToCopy(call.nameIdentifier!!))
        assertEquals(target, provider.qualifiedNameToElement("[proc,target]", project))
    }

    fun testCopiesAndResolvesTypedSymbolName() {
        val symbolFile = myFixture.addFileToProject("symbols/obj.sym", "1\tbronze_sword\n")
        val symbol = PsiTreeUtil.findChildOfType(symbolFile, RsSymSymbol::class.java)!!

        assertEquals("obj:bronze_sword", provider.getQualifiedName(symbol))
        assertEquals(symbol, provider.qualifiedNameToElement("obj:bronze_sword", project))
    }
}
