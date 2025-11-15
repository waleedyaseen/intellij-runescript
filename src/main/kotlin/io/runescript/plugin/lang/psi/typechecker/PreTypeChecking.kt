package io.runescript.plugin.lang.psi.typechecker

import com.intellij.openapi.progress.ProgressIndicatorProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import io.runescript.plugin.lang.psi.*
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostic
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticMessage
import io.runescript.plugin.lang.psi.typechecker.diagnostics.DiagnosticType
import io.runescript.plugin.lang.psi.typechecker.diagnostics.Diagnostics
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableSymbol
import io.runescript.plugin.lang.psi.typechecker.symbol.LocalVariableTable
import io.runescript.plugin.lang.psi.typechecker.trigger.CommandTrigger
import io.runescript.plugin.lang.psi.typechecker.trigger.SubjectMode
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerManager
import io.runescript.plugin.lang.psi.typechecker.trigger.TriggerType
import io.runescript.plugin.lang.psi.typechecker.type.*
import io.runescript.plugin.lang.psi.typechecker.type.wrapped.ArrayType
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

class PreTypeChecking(
    private val triggerManager: TriggerManager,
    private val typeManager: TypeManager,
    private val diagnostics: Diagnostics,
    private val rootTable: LocalVariableTable,
    private val arraysV2: Boolean,
) : RsVisitor() {

    /**
     * A cached reference to a [Type] representing a `type`.
     */
    private val typeType = typeManager.find("type")

    /**
     * A cached reference to a [Type] representing a `category`.
     */
    private val categoryType = typeManager.findOrNull("category")

    /**
     * A cached reference to a [Type] representing an `array`.
     */
    private val arrayType = typeManager.findOrNull("array")

    /**
     * A stack of symbol tables to use through the script file.
     */
    private val tables = ArrayDeque<LocalVariableTable>()

    /**
     * The current active symbol table.
     */
    private val table get() = tables.first()

    init {
        // init with a base table for the file
        tables.addFirst(rootTable.createSubTable())
    }

    /**
     * Wraps [block] with creating a new [SymbolTable], pushing it to [tables] and then popping it back
     * out after the block is run.
     */
    private inline fun createScopedTable(crossinline block: () -> Unit) {
        tables.addFirst(table.createSubTable())
        block()
        tables.removeFirst()
    }

    override fun visitScript(script: RsScript) {
        createScopedTable {
            val trigger = triggerManager.findOrNull(script.triggerNameExpression.text)
            if (trigger == null) {
                script.triggerNameExpression.reportError(
                    DiagnosticMessage.SCRIPT_TRIGGER_INVALID,
                    script.triggerNameExpression.text
                )
            } else {
                script.triggerType = trigger
            }

            // verify subject matched what the trigger requires
            checkScriptSubject(trigger, script)

            // visit the parameters
            val parameters = script.parameterList?.parameterList
            parameters?.visit()

            // specify the parameter types for easy lookup later
            script.parameterType = TupleType.fromList(parameters?.map { it.symbol.type })

            // verify parameters match what the trigger type allows
            checkScriptParameters(trigger, script, parameters)

            // convert return type tokens into actual Types and attach to the script node
            val returnTokens = script.returnList?.typeNameList
            if (!returnTokens.isNullOrEmpty()) {
                val returns = mutableListOf<Type>()
                for (token in returnTokens) {
                    val type = typeManager.findOrNull(token.text, allowArray = arraysV2)
                    if (type == null) {
                        token.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, token.text)
                    }
                    returns += type ?: MetaType.Error
                }
                script.returnType = TupleType.fromList(returns)
            } else {
                // default return based on trigger if the trigger was found
                // triggers that allow returns will default to `unit` instead of `nothing`.
                script.returnType = if (trigger == null) {
                    MetaType.Error
                } else if (trigger.allowReturns) {
                    MetaType.Unit
                } else {
                    MetaType.Nothing
                }
            }

            // verify returns match what the trigger type allows
            checkScriptReturns(trigger, script)

            script.statementList.statementList.visit()

            // set the root symbol table for the script
            script.scope = table
        }
    }

    /**
     * Validates the subject of [script] is allowed following [SubjectMode] for the
     * [trigger].
     */
    private fun checkScriptSubject(trigger: TriggerType?, script: RsScript) {
        val mode = trigger?.subjectMode ?: return
        val subject = script.scriptNameExpression.text

        // name mode allows anything as the subject
        if (mode == SubjectMode.Name) {
            return
        }

        // check for global subject
        if (subject == "_") {
            checkGlobalScriptSubject(trigger, script)
            return
        }

        // check for category reference subject
        if (subject.startsWith("_")) {
            checkCategoryScriptSubject(trigger, script, subject.substring(1))
            return
        }

        // check for reference subject
        checkTypeScriptSubject(trigger, script, subject)
    }

    /**
     * Verifies the trigger subject mode is allowed to be a global subject.
     */
    private fun checkGlobalScriptSubject(trigger: TriggerType, script: RsScript) {
        val mode = trigger.subjectMode

        // trigger only allows global
        if (mode == SubjectMode.None) {
            return
        }

        // subject references a type, verify it allows global subject
        if (mode is SubjectMode.Type) {
            if (!mode.global) {
                script.scriptNameExpression.reportError(DiagnosticMessage.SCRIPT_SUBJECT_NO_GLOBAL, trigger.identifier)
            }
            return
        }
        error(mode)
    }

    /**
     * Verifies the trigger subject mode is allowed to be a category subject.
     */
    private fun checkCategoryScriptSubject(trigger: TriggerType, script: RsScript, subject: String) {
        val mode = trigger.subjectMode
        val categoryType = categoryType ?: error("'category' type not defined.")

        // trigger only allows global
        if (mode == SubjectMode.None) {
            script.scriptNameExpression.reportError(DiagnosticMessage.SCRIPT_SUBJECT_ONLY_GLOBAL, trigger.identifier)
            return
        }

        // subject references a type, verify it allows category subject
        if (mode is SubjectMode.Type) {
            if (!mode.category) {
                script.scriptNameExpression.reportError(DiagnosticMessage.SCRIPT_SUBJECT_NO_CAT, trigger.identifier)
                return
            }

            // attempt to resolve the subject to a category
            resolveSubjectSymbol(script, subject, categoryType)
            return
        }
        error(mode)
    }

    /**
     * Verifies the trigger subject is allowed to refer to a type, category, or global subject.
     */
    private fun checkTypeScriptSubject(trigger: TriggerType, script: RsScript, subject: String) {
        val mode = trigger.subjectMode

        // trigger only allows global
        if (mode == SubjectMode.None) {
            script.scriptNameExpression.reportError(DiagnosticMessage.SCRIPT_SUBJECT_ONLY_GLOBAL, trigger.identifier)
            return
        }

        // subject references a type
        if (mode is SubjectMode.Type) {
            // attempt to resolve the subject to the specified type
            resolveSubjectSymbol(script, subject, mode.type)
            return
        }
        error(mode)
    }

    /**
     * Attempts to find a reference to the subject of a script.
     */
    private fun resolveSubjectSymbol(script: RsScript, subject: String, type: Type) {
        val symbol = RsSymbolIndex.lookup(script.scriptNameExpression, type, subject)
        if (symbol == null) {
            script.scriptNameExpression.reportError(DiagnosticMessage.GENERIC_UNRESOLVED_SYMBOL, subject)
            return
        }
    }

    /**
     * Verifies the [script]s parameter types are what is allowed by the [trigger].
     */
    private fun checkScriptParameters(trigger: TriggerType?, script: RsScript, parameters: List<RsParameter>?) {
        val triggerParameterType = trigger?.parameters
        val scriptParameterType = script.parameterType
        if (trigger != null && !trigger.allowParameters && !parameters.isNullOrEmpty()) {
            parameters.first().reportError(DiagnosticMessage.SCRIPT_TRIGGER_NO_PARAMETERS, trigger.identifier)
        } else if (triggerParameterType != null && scriptParameterType != triggerParameterType) {
            val expectedParameterType = triggerParameterType.representation
            script.reportError(
                DiagnosticMessage.SCRIPT_TRIGGER_EXPECTED_PARAMETERS,
                script.triggerNameExpression.text,
                expectedParameterType,
            )
        }
    }

    /**
     * Verifies the [script] returns what is allowed by the [trigger].
     */
    private fun checkScriptReturns(trigger: TriggerType?, script: RsScript) {
        val triggerReturns = trigger?.returns
        val scriptReturns = script.returnType
        if (trigger != null && !trigger.allowReturns && scriptReturns != MetaType.Nothing) {
            script.reportError(DiagnosticMessage.SCRIPT_TRIGGER_NO_RETURNS, trigger.identifier)
        } else if (triggerReturns != null && scriptReturns != triggerReturns) {
            val expectedReturnTypes = triggerReturns.representation
            script.reportError(
                DiagnosticMessage.SCRIPT_TRIGGER_EXPECTED_RETURNS,
                script.triggerNameExpression.text,
                expectedReturnTypes,
            )
        }
    }

    override fun visitParameter(parameter: RsParameter) {
        val name = parameter.localVariableExpression?.nameLiteral?.text ?: ""
        val typeText = parameter.typeName.text
        val type = typeManager.findOrNull(typeText, allowArray = true)

        // type isn't valid, report the error
        if (type == null) {
            parameter.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeText)
        } else if (!arraysV2 && type is ArrayType && type.inner == PrimitiveType.STRING) {
            // manually disable stringarray since it is marked as allowed now but should
            // remain disabled for old arrays.
            parameter.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeText)
        } else {
            val script = checkNotNull(parameter.parentOfType<RsScript>())
            if (script.triggerType != CommandTrigger) {
                // TODO better way to deal with this instead of hardcoding for each specialized type
                if (type == MetaType.Any || type == typeType || type == arrayType) {
                    parameter.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeText)
                }
            }
        }

        // attempt to insert the local variable into the symbol table and display error if failed to insert
        val symbol = LocalVariableSymbol(name, type ?: MetaType.Error)
        val inserted = table.insert(symbol)
        if (!inserted) {
            parameter.reportError(DiagnosticMessage.SCRIPT_LOCAL_REDECLARATION, name)
        }

        parameter.symbol = symbol
        parameter.localVariableExpression?.type = symbol.type
    }

    override fun visitBlockStatement(blockStatement: RsBlockStatement) {
        createScopedTable {
            // visit inner statements
            blockStatement.statementList.statementList.visit()

            // set the symbol table for the block
            blockStatement.scope = table
        }
    }

    override fun visitSwitchStatement(switchStatement: RsSwitchStatement) {
        val typeName = switchStatement.switch.text.removePrefix("switch_")
        val type = typeManager.findOrNull(typeName)

        // notify invalid type
        if (type == null) {
            switchStatement.switch.reportError(DiagnosticMessage.GENERIC_INVALID_TYPE, typeName)
        } else if (!type.options.allowSwitch) {
            switchStatement.switch.reportError(DiagnosticMessage.SWITCH_INVALID_TYPE, type.representation)
        }

        // visit the condition to resolve any reference
        switchStatement.expression?.accept(this)

        // visit the cases to resolve references in them
        switchStatement.switchCaseList.visit()

        // set the expected type of the switch cases
        switchStatement.type = type ?: MetaType.Error
    }

    override fun visitSwitchCase(switchCase: RsSwitchCase) {
        // visit the keys to set any types that can be set early
        switchCase.expressionList.visit()

        // create a new scope and visit the statements in it
        createScopedTable {
            // visit inner statements
            switchCase.statementList.statementList.visit()

            // set the symbol table for the block
            switchCase.scope = table
        }
    }

    override fun visitElement(element: PsiElement) {
        ProgressIndicatorProvider.checkCanceled()
        for (element in element.children) {
            element.visit()
        }
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.INFO].
     */
    private fun PsiElement.reportInfo(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.INFO, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.WARNING].
     */
    private fun PsiElement.reportWarning(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.WARNING, this, message, *args))
    }

    /**
     * Helper function to report a diagnostic with the type of [DiagnosticType.ERROR].
     */
    private fun PsiElement.reportError(message: String, vararg args: Any) {
        diagnostics.report(Diagnostic(DiagnosticType.ERROR, this, message, *args))
    }

    /**
     * Shortcut to [PsiElement.accept] for nullable nodes.
     */
    fun PsiElement?.visit() {
        if (this == null) {
            return
        }
        accept(this@PreTypeChecking)
    }

    /**
     * Calls [PsiElement.accept] on all nodes in a list.
     */
    fun List<PsiElement>?.visit() {
        if (this == null) {
            return
        }

        for (n in this) {
            n.visit()
        }
    }
}