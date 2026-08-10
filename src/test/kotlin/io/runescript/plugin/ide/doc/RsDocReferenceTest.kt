package io.runescript.plugin.ide.doc

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsDocReferenceTest : RsParserTestCase() {
    fun testParameterRenameUpdatesParamAndParammetaTags() {
        myFixture.configureByText(
            "main.cs2",
            """
            /**
             * @param value Description.
             * @parammeta value rgb
             */
            [proc,main](int ${'$'}<caret>value)
            {
                use(${'$'}value);
            }
            """.trimIndent(),
        )

        myFixture.renameElementAtCaret("colour")

        myFixture.checkResult(
            """
            /**
             * @param colour Description.
             * @parammeta colour rgb
             */
            [proc,main](int ${'$'}colour)
            {
                use(${'$'}colour);
            }
            """.trimIndent(),
        )
    }

    fun testRenameFromParamTagUpdatesSignatureAndUsages() {
        myFixture.configureByText(
            "main.cs2",
            """
            /**
             * @param val<caret>ue Description.
             * @parammeta value rgb
             */
            [proc,main](int ${'$'}value)
            {
                use(${'$'}value);
            }
            """.trimIndent(),
        )

        myFixture.renameElementAtCaret("colour")

        myFixture.checkResult(
            """
            /**
             * @param colour Description.
             * @parammeta colour rgb
             */
            [proc,main](int ${'$'}colour)
            {
                use(${'$'}colour);
            }
            """.trimIndent(),
        )
    }

    fun testDocumentationReferenceAndResolutionAreReused() {
        val targetFile =
            myFixture.addFileToProject(
                "commands.cs2",
                """
                [command,target]
                {
                }
                """.trimIndent(),
            )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                /** Calls [command/target]. */
                [proc,main]
                {
                }
                """.trimIndent(),
            )
        val docName =
            PsiTreeUtil
                .findChildrenOfType(file, RsDocName::class.java)
                .single { it.parent !is RsDocName }
        val target = PsiTreeUtil.findChildOfType(targetFile, RsScript::class.java)
        val reference = docName.reference!!

        assertSame(reference, docName.reference)
        assertSame(target, reference.resolve())
        assertSame(target, reference.resolve())
    }
}
