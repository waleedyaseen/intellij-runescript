package io.runescript.plugin.ide.refactoring

import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsGosubExpression
import io.runescript.plugin.lang.psi.RsScript

class RsMoveScriptHandlerTest : RsParserTestCase() {
    fun testMovesScriptBetweenFilesAndPreservesReferences() {
        val source = myFixture.addFileToProject("source.cs2", "[proc,target]\nreturn;")
        val target = myFixture.addFileToProject("target.cs2", "[proc,existing]\nreturn;")
        val caller = myFixture.configureByText("caller.cs2", "[proc,caller]\n~target();")
        val script = PsiTreeUtil.findChildOfType(source, RsScript::class.java)!!
        val call = PsiTreeUtil.findChildOfType(caller, RsGosubExpression::class.java)!!
        val handler = RsMoveScriptHandler()

        assertTrue(handler.canMove(arrayOf(script), target, null))
        handler.doMove(project, arrayOf(script), target, null)
        PsiDocumentManager.getInstance(project).commitAllDocuments()

        assertFalse(source.text.contains("[proc,target]"))
        val moved = PsiTreeUtil.findChildrenOfType(target, RsScript::class.java).single { it.name == "target" }
        assertEquals(moved, call.reference!!.resolve())
    }

    fun testRejectsSameFileAndDuplicateTarget() {
        val source = myFixture.addFileToProject("source.cs2", "[proc,target]\nreturn;")
        val target = myFixture.addFileToProject("target.cs2", "[proc,target]\nreturn;")
        val script = PsiTreeUtil.findChildOfType(source, RsScript::class.java)!!
        val handler = RsMoveScriptHandler()

        assertFalse(handler.isValidTarget(source, arrayOf(script)))
        assertFalse(handler.isValidTarget(target, arrayOf(script)))
    }
}
