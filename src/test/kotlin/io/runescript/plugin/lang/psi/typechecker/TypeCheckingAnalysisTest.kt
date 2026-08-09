package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.util.PsiTreeUtil
import io.runescript.plugin.ide.inspections.RuneScriptTypeCheckerInspection
import io.runescript.plugin.lang.parser.RsParserTestCase
import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsLocalVariableExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.type.PrimitiveType
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class TypeCheckingAnalysisTest : RsParserTestCase() {
    fun testLocalReferenceDoesNotCrossScriptBoundary() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,first]
                {
                    def_int ${"$"}value = 1;
                }

                [proc,second]
                {
                    ${"$"}value = 2;
                }
                """.trimIndent(),
            )
        val usages = PsiTreeUtil.findChildrenOfType(file, RsLocalVariableExpression::class.java)

        assertNull(usages.last().reference?.resolve())
    }

    fun testDynamicReferenceReusesSymbolResolvedByTypeChecking() {
        val targetFile =
            myFixture.addFileToProject(
                "commands.cs2",
                """
                [command,target]()(int)
                {
                }
                """.trimIndent(),
            )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = target;
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!
        val target = PsiTreeUtil.findChildOfType(targetFile, RsScript::class.java)!!
        val expression = PsiTreeUtil.findChildOfType(file, RsDynamicExpression::class.java)!!

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        val initialData = script.typeCheckerData
        assertSame(target, expression.typeCheckedResolvedSymbol)
        assertSame(target, expression.reference?.resolve())
        assertEmpty(TypeCheckingUtil.typeCheck(script))
        myFixture.enableInspections(RuneScriptTypeCheckerInspection())
        myFixture.doHighlighting()
        assertSame(initialData, script.typeCheckerData)
    }

    fun testConcurrentRequestsPublishCompleteAnalysis() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                    ${"$"}value = calc(${"$"}value + 1);
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!
        val executor = Executors.newFixedThreadPool(4)
        val start = CountDownLatch(1)

        try {
            val requests =
                List(8) {
                    executor.submit<List<Diagnostic>> {
                        start.await()
                        ReadAction.computeBlocking<List<Diagnostic>, RuntimeException> {
                            TypeCheckingUtil.typeCheck(script)
                        }
                    }
                }
            start.countDown()

            for (request in requests) {
                assertEmpty(request.get())
            }
        } finally {
            executor.shutdownNow()
        }

        val expression = PsiTreeUtil.findChildrenOfType(script, RsLocalVariableExpression::class.java).last()
        assertEquals(PrimitiveType.INT, expression.typeCheckedType)
    }

    fun testReferenceInstanceAndResolutionAreReused() {
        val target =
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
                [proc,main]
                {
                    target();
                }
                """.trimIndent(),
            )
        val call = PsiTreeUtil.findChildOfType(file, RsCommandExpression::class.java)!!
        val reference = call.reference!!

        assertSame(reference, call.reference)
        assertEquals(PsiTreeUtil.findChildOfType(target, RsScript::class.java), reference.resolve())
        assertEquals(reference.resolve(), reference.resolve())
    }

    fun testAnalysisIsInvalidatedWhenReferencedSignatureChanges() {
        val target =
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
                [proc,main]
                {
                    target();
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        val initialData = script.typeCheckerData

        WriteCommandAction.runWriteCommandAction(project) {
            target.viewProvider.document!!.setText(
                """
                [command,target](int ${"$"}value)
                {
                }
                """.trimIndent(),
            )
            PsiDocumentManager.getInstance(project).commitAllDocuments()
        }

        assertNotEmpty(TypeCheckingUtil.typeCheck(script))
        assertNotSame(initialData, script.typeCheckerData)
    }

    fun testAnalysisIsRetainedWhenUnrelatedBodyChanges() {
        val unrelated =
            myFixture.addFileToProject(
                "unrelated.cs2",
                """
                [proc,unrelated]
                {
                    target(1);
                }
                """.trimIndent(),
            )
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        val initialData = script.typeCheckerData

        WriteCommandAction.runWriteCommandAction(project) {
            unrelated.viewProvider.document!!.setText(
                """
                [proc,unrelated]
                {
                    target(2);
                }
                """.trimIndent(),
            )
            PsiDocumentManager.getInstance(project).commitAllDocuments()
        }

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        assertSame(initialData, script.typeCheckerData)
    }

    fun testAnalysisIsRetainedWhenUnrelatedLanguageStubChanges() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    def_int ${"$"}value = 1;
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        val initialData = script.typeCheckerData
        myFixture.addFileToProject("Unrelated.java", "class Unrelated {}")

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        assertSame(initialData, script.typeCheckerData)
    }

    fun testUnresolvedAnalysisIsInvalidatedWhenTargetIsAdded() {
        val file =
            myFixture.configureByText(
                "main.cs2",
                """
                [proc,main]
                {
                    target();
                }
                """.trimIndent(),
            )
        val script = PsiTreeUtil.findChildOfType(file, RsScript::class.java)!!

        assertNotEmpty(TypeCheckingUtil.typeCheck(script))
        val initialData = script.typeCheckerData

        myFixture.addFileToProject(
            "commands.cs2",
            """
            [command,target]
            {
            }
            """.trimIndent(),
        )

        assertEmpty(TypeCheckingUtil.typeCheck(script))
        assertNotSame(initialData, script.typeCheckerData)
    }
}
