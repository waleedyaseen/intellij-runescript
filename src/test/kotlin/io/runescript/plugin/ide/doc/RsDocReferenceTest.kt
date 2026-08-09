package io.runescript.plugin.ide.doc

import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.doc.psi.impl.RsDocName
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsScript

class RsDocReferenceTest : RsParserTestCase() {
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
