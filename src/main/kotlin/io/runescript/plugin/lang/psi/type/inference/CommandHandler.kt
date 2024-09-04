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
            ?.map { RsPrimitiveType.lookup(it.typeName!!.text) }
            ?.flatten()
            ?: emptyArray<RsType>()
        checkArgumentList(o.argumentList, parameterTypes)

        val returnTypes = reference
            .returnList
            ?.typeNameList
            ?.map { RsPrimitiveType.lookupReferencable(it.text) }
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