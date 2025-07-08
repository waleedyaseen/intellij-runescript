package io.runescript.plugin.lang.psi.type.inference

import io.runescript.plugin.lang.psi.RsCommandExpression
import io.runescript.plugin.lang.psi.RsDynamicExpression
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.type.*
import io.runescript.plugin.symbollang.psi.index.RsSymbolIndex

sealed interface CommandHandler {
    fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression)
}

data object DefaultCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val parameterTypes = reference
            .parameterList
            ?.parameterList
            ?.map { RsPrimitiveType.lookup(it.typeName.text) }
            ?.flatten()
            ?: emptyArray<RsType>()
        checkArgumentList(o.argumentList, parameterTypes)

        val returnTypes = reference
            .returnList
            ?.typeNameList
            ?.map { RsPrimitiveType.lookup(it.text) }
            ?: emptyList()

        if (returnTypes.isEmpty()) {
            o.type = RsUnitType
        } else if (returnTypes.size == 1) {
            o.type = returnTypes[0]
        } else {
            o.type = RsTupleType(returnTypes.flatten())
        }
    }
}

class ParamCommandHandler(private val subjectType: RsPrimitiveType) : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val arguments = o.argumentList.expressionList
        var outputType: RsType? = null
        if (o.argumentList.expressionList.size >= 2) {
            arguments[1].typeHint = RsPrimitiveType.PARAM
            arguments[1].accept(this)
            if (arguments[1] is RsDynamicExpression) {
                val parameterSymbol =
                    RsSymbolIndex.lookup(arguments[1], RsPrimitiveType.PARAM, arguments[1].text)
                if (parameterSymbol == null || parameterSymbol.fieldList.size < 3) {
                    arguments[1].error("Reference to a parameter with an invalid definition.")
                } else {
                    val parameterType = RsPrimitiveType.lookupReferencableOrNull(parameterSymbol.fieldList[2].text)
                    if (parameterType == null) {
                        arguments[1].error("Reference to a parameter with an invalid definition.")
                    }
                    outputType = parameterType
                }
            } else {
                arguments[1].error("Non-constant param references are not allowed in here.")
            }
        }
        val parameterTypes = arrayOf<RsType>(
            subjectType,
            RsPrimitiveType.PARAM,
        )
        checkArgumentList(o.argumentList, parameterTypes)
        o.type = outputType ?: RsErrorType
    }

    companion object {
        val STRUCT_PARAM = ParamCommandHandler(RsPrimitiveType.STRUCT)
        val NC_PARAM = ParamCommandHandler(RsPrimitiveType.NPC)
        val LC_PARAM = ParamCommandHandler(RsPrimitiveType.LOC)
        val OC_PARAM = ParamCommandHandler(RsPrimitiveType.OBJ)
    }
}

class IfParamCommandHandler(private val cc: Boolean) : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val arguments = o.argumentList.expressionList
        var outputType: RsType? = null

        // Handle param reference (first argument)
        if (arguments.isNotEmpty()) {
            arguments[0].typeHint = RsPrimitiveType.PARAM
            arguments[0].accept(this)
            if (arguments[0] is RsDynamicExpression) {
                val parameterSymbol = RsSymbolIndex.lookup(arguments[0], RsPrimitiveType.PARAM, arguments[0].text)
                if (parameterSymbol == null || parameterSymbol.fieldList.size < 3) {
                    arguments[0].error("Reference to a parameter with an invalid definition.")
                } else {
                    val parameterType = RsPrimitiveType.lookupReferencableOrNull(parameterSymbol.fieldList[2].text)
                    if (parameterType == null) {
                        arguments[0].error("Reference to a parameter with an invalid definition.")
                    }
                    outputType = parameterType
                }
            } else {
                arguments[0].error("Non-constant param references are not allowed in here.")
            }
        }

        // Define parameter types based on whether it's cc or if_param
        val parameterTypes: Array<RsType> = if (!cc) {
            arrayOf(
                RsPrimitiveType.PARAM,
                RsPrimitiveType.COMPONENT,
                RsPrimitiveType.INT
            )
        } else {
            arrayOf(RsPrimitiveType.PARAM)
        }

        // Check argument types
        checkArgumentList(o.argumentList, parameterTypes)

        // Set the output type
        o.type = outputType ?: RsErrorType
    }

    companion object {
        val IF_PARAM = IfParamCommandHandler(false)
        val CC_PARAM = IfParamCommandHandler(true)
    }
}

class IfSetParamCommandHandler(private val cc: Boolean) : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val arguments = o.argumentList.expressionList

        // Check first argument (param reference)
        if (arguments.isEmpty()) {
            o.error("Missing parameter reference")
            o.type = RsUnitType
            return
        }

        // Handle param reference
        arguments[0].typeHint = RsPrimitiveType.PARAM
        arguments[0].accept(this)
        var parameterType: RsType? = null

        if (arguments[0] is RsDynamicExpression) {
            val parameterSymbol = RsSymbolIndex.lookup(arguments[0], RsPrimitiveType.PARAM, arguments[0].text)
            if (parameterSymbol == null || parameterSymbol.fieldList.size < 3) {
                arguments[0].error("Reference to a parameter with an invalid definition.")
            } else {
                parameterType = RsPrimitiveType.lookupReferencableOrNull(parameterSymbol.fieldList[2].text)
                if (parameterType == null) {
                    arguments[0].error("Reference to a parameter with an invalid definition.")
                }
            }
        } else {
            arguments[0].error("Non-constant param references are not allowed in here.")
        }

        // Check second argument (value to set) against the parameter type
        if (arguments.size > 1 && parameterType != null) {
            arguments[1].typeHint = parameterType
            arguments[1].accept(this)
            if (arguments[1].type != parameterType) {
                arguments[1].error("Type mismatch: expected ${parameterType.representation}, got ${arguments[1].type?.representation}")
            }
        }

        // Define and check parameter types based on whether it's cc or if_setparam
        val parameterTypes = if (!cc) {
            arrayOf(
                RsPrimitiveType.PARAM,
                parameterType ?: RsErrorType,
                RsPrimitiveType.COMPONENT,
                RsPrimitiveType.INT
            )
        } else {
            arrayOf(
                RsPrimitiveType.PARAM,
                parameterType ?: RsErrorType
            )
        }

        // Check all arguments
        checkArgumentList(o.argumentList, parameterTypes)

        // Set return type to Unit as this is a setter
        o.type = RsUnitType
    }

    companion object {
        val IF_SETPARAM = IfSetParamCommandHandler(false)
        val CC_SETPARAM = IfSetParamCommandHandler(true)
    }
}

class DbFindCommandHandler(private val withCount: Boolean) : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val arguments = o.argumentList.expressionList
        var keyType: RsType? = null
        if (arguments[0] is RsDynamicExpression) {
            val dbTableSym = RsSymbolIndex.lookup(arguments[0], RsPrimitiveType.DBCOLUMN, arguments[0].text)
            if (dbTableSym == null || dbTableSym.fieldList.size < 3) {
                arguments[0].error("Reference to a dbtable with an invalid definition.")
            } else {
                val typesField = dbTableSym.fieldList[2].text.split(",").map {
                    RsPrimitiveType.lookupReferencableOrNull(it) ?: RsErrorType
                }
                if (typesField.isEmpty() || typesField.any { it is RsErrorType }) {
                    arguments[0].error("Reference to a dbtable with an invalid definition.")
                } else {
                    keyType = typesField.joined()
                }
            }
        } else {
            arguments[1].error("Non-constant dbtable references are not allowed in here.")
        }
        if (keyType != null && keyType !is RsPrimitiveType) {
            arguments[1].error("Tuple types are not allowed to be used in db_find commands.")
        }
        val parameterTypes = arrayOf(
            RsPrimitiveType.DBCOLUMN,
            keyType ?: RsErrorType
        )
        checkArgumentList(o.argumentList, parameterTypes)
        if (withCount) {
            o.type = RsPrimitiveType.INT
        } else {
            o.type = RsUnitType
        }
    }

    companion object {
        val DB_FIND = DbFindCommandHandler(false)
        val DB_FIND_WITH_COUNT = DbFindCommandHandler(true)
        val DB_FIND_REFINE = DbFindCommandHandler(false)
        val DB_FIND_REFINE_WITH_COUNT = DbFindCommandHandler(true)
    }
}

data object DbGetFieldCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(
        reference: RsScript,
        o: RsCommandExpression
    ) {
        val arguments = o.argumentList.expressionList
        var outputType: RsType? = null
        if (arguments[1] is RsDynamicExpression) {
            val dbTableSym = RsSymbolIndex.lookup(arguments[1], RsPrimitiveType.DBCOLUMN, arguments[1].text)
            if (dbTableSym == null || dbTableSym.fieldList.size < 3) {
                arguments[1].error("Reference to a dbtable with an invalid definition.")
            } else {
                val typesField = dbTableSym.fieldList[2].text.split(",").map {
                    RsPrimitiveType.lookupReferencableOrNull(it) ?: RsErrorType
                }
                if (typesField.isEmpty() || typesField.any { it is RsErrorType }) {
                    arguments[1].error("Reference to a dbtable with an invalid definition.")
                } else {
                    outputType = typesField.joined()
                }
            }
        } else {
            arguments[1].error("Non-constant dbtable references are not allowed in here.")
        }

        val parameterTypes = arrayOf<RsType>(
            RsPrimitiveType.DBROW,
            RsPrimitiveType.DBCOLUMN,
            RsPrimitiveType.INT,
        )
        checkArgumentList(o.argumentList, parameterTypes)
        o.type = outputType ?: RsErrorType
    }
}

data object EnumCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(
        reference: RsScript,
        o: RsCommandExpression
    ) {
        val arguments = o.argumentList.expressionList
        var inputType: RsType? = null
        if (arguments.size >= 1) {
            arguments[0].typeHint = RsTypeType
            arguments[0].accept(this)
            if (arguments[0] is RsDynamicExpression) {
                inputType = RsPrimitiveType.lookupReferencableOrNull(arguments[0].text) ?: RsErrorType
            }
        }
        var outputType: RsType? = null
        if (arguments.size >= 2) {
            arguments[1].typeHint = RsTypeType
            arguments[1].accept(this)
            if (arguments[1] is RsDynamicExpression) {
                outputType = RsPrimitiveType.lookupReferencableOrNull(arguments[1].text) ?: RsErrorType
            }
        }
        val parameterTypes = arrayOf(
            RsTypeType,
            RsTypeType,
            RsPrimitiveType.ENUM,
            inputType ?: RsErrorType
        )
        checkArgumentList(o.argumentList, parameterTypes)
        o.type = outputType ?: RsErrorType
    }
}


data object EnumGetReverseIndexCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(
        reference: RsScript,
        o: RsCommandExpression
    ) {
        val arguments = o.argumentList.expressionList
        var outputType: RsType? = null
        if (arguments.size >= 1) {
            arguments[0].typeHint = RsTypeType
            arguments[0].accept(this)
            if (arguments[0] is RsDynamicExpression) {
                outputType = RsPrimitiveType.lookupReferencableOrNull(arguments[0].text) ?: RsErrorType
            }
        }
        var inputType: RsType? = null
        if (arguments.size >= 2) {
            arguments[1].typeHint = RsTypeType
            arguments[1].accept(this)
            if (arguments[1] is RsDynamicExpression) {
                inputType = RsPrimitiveType.lookupReferencableOrNull(arguments[1].text) ?: RsErrorType
            }
        }
        val parameterTypes = arrayOf(
            RsTypeType,
            RsTypeType,
            RsPrimitiveType.ENUM,
            outputType ?: RsErrorType,
            RsPrimitiveType.INT,
        )
        checkArgumentList(o.argumentList, parameterTypes)
        o.type = inputType ?: RsErrorType
    }
}

data object DumpCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(
        reference: RsScript,
        o: RsCommandExpression
    ) {
        val arguments = o.argumentList.expressionList
        if (arguments.isEmpty()) {
            o.error("Type mismatch: '<unit>' was given but 'any...' was expected.")
        } else {
            for (argument in arguments) {
                argument.typeHint = RsAnyType
                argument.accept(this)

                val type = argument.type
                if (type is RsTupleType) {
                    argument.error("Unable to dump multi-value types: %s".format(type.representation))
                } else if (type == RsUnitType) {
                    argument.error("Unable to debug expression with no return value.")
                }
            }
        }

        o.type = RsPrimitiveType.STRING
    }
}

data object CcCreateCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(
        reference: RsScript,
        o: RsCommandExpression
    ) {
        val arguments = o.argumentList.expressionList
        val parameterTypes = if (arguments.size == 4) {
            arrayOf<RsType>(
                RsPrimitiveType.COMPONENT,
                RsPrimitiveType.INT,
                RsPrimitiveType.INT,
                RsPrimitiveType.BOOLEAN
            )
        } else {
            arrayOf<RsType>(
                RsPrimitiveType.COMPONENT,
                RsPrimitiveType.INT,
                RsPrimitiveType.INT
            )
        }
        checkArgumentList(o.argumentList, parameterTypes)
        o.type = RsUnitType
    }
}

data object ArrayPushCommandHandler : CommandHandler {
    override fun RsTypeInferenceVisitor.inferTypes(reference: RsScript, o: RsCommandExpression) {
        val arguments = o.argumentList.expressionList
        if (arguments.size != 2) {
            o.error("Array push requires exactly 2 arguments: array and value")
            o.type = RsUnitType
            return
        }

        // First check array argument with Any type hint to get its real type
        val arrayArg = arguments[0]
        arrayArg.typeHint = RsArrayType(RsAnyType)
        arrayArg.accept(this)

        // Verify it's an array type
        val arrayType = arrayArg.type
        if (arrayType !is RsArrayType) {
            arrayArg.error("First argument must be an array")
            o.type = RsUnitType
            return
        }

        // Get the inner type of the array and use it to check the value argument
        val elementType = arrayType.elementType
        val valueArg = arguments[1]
        valueArg.typeHint = elementType
        valueArg.accept(this)

        if (valueArg.type != elementType) {
            valueArg.error("Type mismatch: expected ${elementType.representation}, got ${valueArg.type?.representation}")
        }

        o.type = RsUnitType
    }
}
