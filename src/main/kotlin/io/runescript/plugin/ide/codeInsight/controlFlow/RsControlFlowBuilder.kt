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
            o.parameterList?.parameterList?.forEach { parameter ->
                parameter.accept(this)
            }
            o.statementList.accept(this)
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
            o.expressionList.forEach { it.accept(this) }
            addVariableDefinition(o)
        }

        override fun visitLocalVariableExpression(o: RsLocalVariableExpression) {
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitArrayVariableDeclarationStatement(o: RsArrayVariableDeclarationStatement) {
            if (o.expressionList.size > 1) {
                o.expressionList[1].accept(this)
            }
            o.expressionList[0].accept(this)
            addInstruction(o)
        }

        override fun visitAssignmentStatement(o: RsAssignmentStatement) {
            // Same as calling it for leftExpressions then for rightExpressions
            o.expressionList.forEach { it.accept(this) }
            addInstruction(o)
        }

        override fun visitArrayAccessExpression(o: RsArrayAccessExpression) {
            o.expressionList[0].accept(this)
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

        override fun visitHookFragment(o: RsHookFragment) {
            o.hookTransmitList?.accept(this)
            o.argumentList?.accept(this)
            o.nameLiteral.accept(this)
            addInstruction(o)
        }

        override fun visitHookTransmitList(o: RsHookTransmitList) {
            o.dynamicExpressionList.forEach { it.accept(this) }
        }

        override fun visitCommandExpression(o: RsCommandExpression) {
            o.argumentList.accept(this)
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

        override fun visitEmptyStatement(o: RsEmptyStatement) {
            addInstruction(o)
        }

        override fun visitIfStatement(o: RsIfStatement) {
            // ------------------------
            // Case 1:false statement is not present
            //  if (expr) <ifStart> {
            //    <ifTrue>
            //  }
            //  <ifEnd>
            //  <ifStart> -> <ifEnd>
            //  <ifTrue> -> <ifEnd>
            // ------------------------
            // Case 2: false statement is present
            //  if (expr) <ifStart> {
            //    <ifTrue>
            //  } else {
            //    <ifFalse>
            //  }
            //  <ifEnd>
            //  <ifTrue> -> <ifEnd>
            //  <ifFalse> -> <ifEnd>
            //  <ifStart> -> <ifTrue>
            //  <ifStart> -> <ifFalse>
            // ------------------------
            val trueStatement = o.trueStatement
            val falseStatement = o.falseStatement

            o.expression?.accept(this)
            val ifStartBlock = addInstruction(o)
            builder.flowAbrupted()

            val ifTrueBlockStart = addInstruction(null)
            trueStatement?.accept(this)
            val ifTrueBlockEnd = builder.prevInstruction
            builder.flowAbrupted()

            val (ifFalseBlockStart, ifFalseBlockEnd) = if (falseStatement != null) {
                val blockStart = addInstruction(null)
                falseStatement.accept(this)
                val blockEnd = builder.prevInstruction
                builder.flowAbrupted()
                blockStart to blockEnd
            } else {
                null to null
            }

            val ifEndBlock = addInstruction(null)
            builder.prevInstruction = ifEndBlock

            if (falseStatement != null) {
                builder.addEdge(ifStartBlock, ifTrueBlockStart)
                builder.addEdge(ifStartBlock, ifFalseBlockStart)
                if (ifTrueBlockEnd != null) {
                    builder.addEdge(ifTrueBlockEnd, ifEndBlock)
                }
                if (ifFalseBlockEnd != null) {
                    builder.addEdge(ifFalseBlockEnd, ifEndBlock)
                }
            } else {
                builder.addEdge(ifStartBlock, ifTrueBlockStart)
                builder.addEdge(ifStartBlock, ifEndBlock)
                if (ifTrueBlockEnd != null) {
                    builder.addEdge(ifTrueBlockEnd, ifEndBlock)
                }
            }
        }

        override fun visitWhileStatement(o: RsWhileStatement) {
            val whileStart = addInstruction(o)
            o.expression?.accept(this)
            o.statement?.accept(this)
            // Jump from last instruction in the body to the beginning of the while
            builder.addEdge(builder.instructions.last(), whileStart)
            builder.prevInstruction = whileStart
        }

        override fun visitSwitchStatement(o: RsSwitchStatement) {
            o.expression?.accept(this)
            val switchStart = addInstruction(o)
            val caseEnds = mutableSetOf<Instruction>()
            var isExhaustive = false
            o.switchCaseList.forEach {
                builder.prevInstruction = switchStart
                addInstruction(null)
                it.accept(this)
                if (builder.prevInstruction != null) {
                    caseEnds += builder.prevInstruction
                }
                if (it.expressionList.any { expression -> expression is RsSwitchCaseDefaultExpression }) {
                    isExhaustive = true
                }
            }
            if (caseEnds.isNotEmpty() || !isExhaustive) {
                builder.prevInstruction = switchStart
                val switchEnd = addInstruction(null)
                caseEnds.forEach { caseEnd ->
                    builder.addEdge(caseEnd, switchEnd)
                }
            }
        }

        override fun visitSwitchCase(o: RsSwitchCase) {
            o.expressionList.forEach { it.accept(this) }
            o.statementList.accept(this)
        }

        override fun visitSwitchCaseDefaultExpression(o: RsSwitchCaseDefaultExpression) {
            addInstruction(o)
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

        override fun visitCoordLiteralExpression(o: RsCoordLiteralExpression) {
            addInstruction(o)
        }

        override fun visitStringLiteralExpression(o: RsStringLiteralExpression) {
            o.stringLiteralContent.accept(this)
            addInstruction(o)
        }

        override fun visitStringLiteralContent(o: RsStringLiteralContent) {
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