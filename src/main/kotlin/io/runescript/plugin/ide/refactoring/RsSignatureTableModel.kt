package io.runescript.plugin.ide.refactoring

import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.typechecker.type.BaseVarType
import io.runescript.plugin.lang.psi.typechecker.type.TypeManager
import javax.swing.table.AbstractTableModel

internal class RsSignatureTableModel(
    script: RsScript,
) : AbstractTableModel() {
    private val typeManager = script.typeManager
    private val rows =
        script.parameterList
            ?.parameterList
            .orEmpty()
            .mapIndexedTo(mutableListOf()) { index, parameter ->
                Row(
                    typeName = parameter.typeName.text,
                    name = parameter.localVariableExpression?.name.orEmpty(),
                    newArgumentValue = null,
                    originalIndex = index,
                )
            }

    override fun getRowCount(): Int = rows.size

    override fun getColumnCount(): Int = 2

    override fun getColumnName(column: Int): String = COLUMN_NAMES[column]

    override fun getValueAt(
        rowIndex: Int,
        columnIndex: Int,
    ): String =
        when (columnIndex) {
            TYPE_COLUMN -> rows[rowIndex].typeName
            NAME_COLUMN -> "$${rows[rowIndex].name}"
            else -> error("Unknown column $columnIndex")
        }

    override fun setValueAt(
        value: Any?,
        rowIndex: Int,
        columnIndex: Int,
    ) {
        val text = value?.toString().orEmpty().trim()
        val current = rows[rowIndex]
        rows[rowIndex] =
            when (columnIndex) {
                TYPE_COLUMN -> {
                    current.copy(
                        typeName = text,
                        newArgumentValue = current.newArgumentValue?.let { defaultArgumentValue(typeManager, text) },
                    )
                }

                NAME_COLUMN -> {
                    current.copy(name = text.removePrefix("$"))
                }

                else -> {
                    error("Unknown column $columnIndex")
                }
            }
        fireTableCellUpdated(rowIndex, columnIndex)
    }

    override fun isCellEditable(
        rowIndex: Int,
        columnIndex: Int,
    ): Boolean = true

    fun parameters(): List<RsSignatureParameter> =
        rows.map { row ->
            RsSignatureParameter(
                typeName = row.typeName,
                name = row.name,
                newArgumentValue = row.newArgumentValue,
                originalIndex = row.originalIndex,
            )
        }

    fun addParameter(): Int {
        val index = rows.size
        rows += Row("int", uniqueParameterName(), defaultArgumentValue(typeManager, "int"), null)
        fireTableRowsInserted(index, index)
        return index
    }

    fun removeParameter(index: Int) {
        if (index !in rows.indices) return
        rows.removeAt(index)
        fireTableRowsDeleted(index, index)
    }

    fun moveParameter(
        from: Int,
        to: Int,
    ): Int {
        if (from !in rows.indices || to !in rows.indices || from == to) return from
        val row = rows.removeAt(from)
        rows.add(to, row)
        fireTableDataChanged()
        return to
    }

    private fun uniqueParameterName(): String {
        val names = rows.mapTo(hashSetOf(), Row::name)
        var suffix = 1
        while (true) {
            val candidate = if (suffix == 1) "parameter" else "parameter$suffix"
            if (candidate !in names) return candidate
            suffix++
        }
    }

    private data class Row(
        val typeName: String,
        val name: String,
        val newArgumentValue: String?,
        val originalIndex: Int?,
    )

    companion object {
        const val TYPE_COLUMN = 0
        const val NAME_COLUMN = 1

        private val COLUMN_NAMES = arrayOf("Type", "Name")

        private fun defaultArgumentValue(
            typeManager: TypeManager,
            typeName: String,
        ): String {
            val type = typeManager.findOrNull(typeName, allowArray = true) ?: return "0"
            return when (type.baseType) {
                BaseVarType.STRING -> "\"${type.defaultValue?.toString().orEmpty().replace("\\", "\\\\").replace("\"", "\\\"")}\""
                BaseVarType.LONG -> "${type.defaultValue ?: -1}L"
                else -> type.defaultValue?.toString() ?: "0"
            }
        }
    }
}
