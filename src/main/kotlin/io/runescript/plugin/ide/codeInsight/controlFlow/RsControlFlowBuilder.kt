package io.runescript.plugin.ide.codeInsight.controlFlow

import com.intellij.codeInsight.controlflow.ControlFlowBuilder
import com.intellij.codeInsight.controlflow.Instruction
import com.intellij.psi.PsiElement
import io.runescript.plugin.lang.psi.*

class RsControlFlowBuilder : ControlFlowBuilder() {

    fun build(element: PsiElement) = build(RsControlFlowBuilderVisitor(this), element)

    internal class RsControlFlowBuilderVisitor(private val builder: RsControlFlowBuilder) : RsRecursiveVisitor() {

        override fun visitScript(o: RsScript) {
            addScriptDefinitionInstruction(o)
            o.scriptHeader.parameterList?.parameterList?.forEach { parameter ->
                parameter.accept(this)
            }
            o.statementList.accept(this)
        }

        override fun visitScriptHeader(o: RsScriptHeader) {
            // Nothing
        }

        override fun visitScriptName(o: RsScriptName) {
            // Nothing
        }

        override fun visitParameterList(o: RsParameterList) {
            // Nothing
        }

        override fun visitParameter(o: RsParameter) {
            addVariableDefinition(o)
        }

        override fun visitStatementList(o: RsStatementList) {
            addInstruction(o)
            o.statementList.forEach { it.accept(this) }
        }

        override fun visitBlockStatement(o: RsBlockStatement) {
            addInstruction(o)
            o.statementList.accept(this)
        }

        override fun visitLocalVariableDeclarationStatement(o: RsLocalVariableDeclarationStatement) {
            o.expressionList[0].accept(this)
            o.expressionList[1].accept(this)
            addVariableDefinition(o)
        }

        override fun visitLocalVariableAssignmentStatement(o: RsLocalVariableAssignmentStatement) {
            o.expressionList[1].accept(this)
            o.expressionList[0].accept(this)
            addInstruction(o)
        }

        override fun visitLocalVariableExpression(o: RsLocalVariableExpression) {
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitArrayVariableDeclarationStatement(o: RsArrayVariableDeclarationStatement) {
            o.expressionList[1].accept(this)
            o.expressionList[0].accept(this)
            addInstruction(o)
        }

        override fun visitArrayVariableAssignmentStatement(o: RsArrayVariableAssignmentStatement) {
            o.expressionList[1].accept(this)
            o.expressionList[0].accept(this)
            addInstruction(o)
        }

        override fun visitArrayVariableExpression(o: RsArrayVariableExpression) {
            o.expressionList[1].accept(this)
            o.expressionList[0].accept(this)
        }

        override fun visitScopedVariableAssignmentStatement(o: RsScopedVariableAssignmentStatement) {
            o.expressionList[0].accept(this)
            o.expressionList[1].accept(this)
            addInstruction(o)
        }

        override fun visitScopedVariableExpression(o: RsScopedVariableExpression) {
            o.nameLiteral.accept(this)
        }

        override fun visitConstantExpression(o: RsConstantExpression) {
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitDynamicExpression(o: RsDynamicExpression) {
            o.nameLiteral.accept(this)
        }

        override fun visitParExpression(o: RsParExpression) {
            o.expression.accept(this)
        }

        override fun visitGosubExpression(o: RsGosubExpression) {
            o.argumentList?.accept(this)
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitCommandExpression(o: RsCommandExpression) {
            o.argumentList?.accept(this)
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitArithmeticValueExpression(o: RsArithmeticValueExpression) {
            o.expression?.accept(this)
        }

        override fun visitRelationalValueExpression(o: RsRelationalValueExpression) {
            o.expression?.accept(this)
        }

        override fun visitCalcExpression(o: RsCalcExpression) {
            o.expression.accept(this)
            addInstruction(o)
        }

        override fun visitConditionExpression(o: RsConditionExpression) {
            o.left.accept(this)
            o.right.accept(this)
            o.conditionOp.accept(this)
            addInstruction(o)
        }

        override fun visitConditionOp(o: RsConditionOp) {
            addInstruction(o)
        }

        override fun visitArithmeticExpression(o: RsArithmeticExpression) {
            o.left.accept(this)
            o.right.accept(this)
            o.arithmeticOp.accept(this)
            addInstruction(o)
        }

        override fun visitArithmeticOp(o: RsArithmeticOp) {
            addInstruction(o)
        }


        override fun visitExpressionStatement(o: RsExpressionStatement) {
            o.expression.accept(this)
            addInstruction(o)
        }

        override fun visitIfStatement(o: RsIfStatement) {
            val ifStart = addInstruction(o)
            o.expression.accept(this)
            o.statement.accept(this)
            val ifEnd = addInstruction(null)
            // If the flow has been abrupt, there is no need to create an edge.
            if (builder.prevInstruction != null) {
                builder.addEdge(builder.prevInstruction, ifEnd)
            }
            builder.addEdge(ifStart, ifEnd)
        }

        override fun visitWhileStatement(o: RsWhileStatement) {
            val whileStart = addInstruction(o)
            o.expression.accept(this)
            o.statement.accept(this)
            // Jump from last instruction in the body to the beginning of the while
            builder.addEdge(builder.instructions.last(), whileStart)
            builder.prevInstruction = whileStart
        }

        override fun visitSwitchStatement(o: RsSwitchStatement) {
            o.expression.accept(this)
            val switchStart = addInstruction(o)
            val caseEnds = mutableSetOf<Instruction>()
            o.switchCaseList.forEach {
                builder.prevInstruction = switchStart
                addInstruction(null)
                it.accept(this)
                if (builder.prevInstruction != null) {
                    caseEnds += builder.prevInstruction
                }
            }
            builder.prevInstruction = switchStart
            val switchEnd = addInstruction(null)
            caseEnds.forEach { caseEnd ->
                builder.addEdge(caseEnd, switchEnd)
            }
        }

        override fun visitSwitchCase(o: RsSwitchCase) {
            o.expressionList.forEach { it.accept(this) }
            o.statementList.accept(this)
        }

        override fun visitReturnStatement(o: RsReturnStatement) {
            o.expressionList.forEach { it.accept(this) }
            addInstruction(o)
            builder.flowAbrupted()
        }

        override fun visitArgumentList(o: RsArgumentList) {
            o.expressionList.forEach { it.accept(this) }
        }

        override fun visitNameLiteral(o: RsNameLiteral) {
            addInstruction(o)
        }

        override fun visitBooleanLiteralExpression(o: RsBooleanLiteralExpression) {
            addInstruction(o)
        }

        override fun visitIntegerLiteralExpression(o: RsIntegerLiteralExpression) {
            addInstruction(o)
        }

        override fun visitStringLiteralExpression(o: RsStringLiteralExpression) {
            o.stringInterpolationExpressionList.forEach { it.accept(this) }
            addInstruction(o)
        }

        override fun visitStringInterpolationExpression(o: RsStringInterpolationExpression) {
            o.expression.accept(this)
        }

        override fun visitNullLiteralExpression(o: RsNullLiteralExpression) {
            addInstruction(o)
        }

        private fun addScriptDefinitionInstruction(element: PsiElement?): RsInstruction {
            return addInstruction(element)
        }

        private fun addVariableDefinition(element: PsiElement?): RsInstruction {
            return addInstruction(element)
        }

        private fun addInstruction(element: PsiElement?): RsInstruction {
            val instruction = RsInstruction(builder, element)
            builder.addNode(instruction)
            return instruction
        }
    }
}