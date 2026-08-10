package io.runescript.plugin.ide.refactoring

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ColoredTableCellRenderer
import com.intellij.ui.EditorTextField
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.TitledSeparator
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import io.runescript.plugin.ide.filetypes.RsFileType
import io.runescript.plugin.ide.highlight.RsSyntaxHighlighterColors
import io.runescript.plugin.ide.neptune.typeManager
import io.runescript.plugin.lang.psi.RsScript
import io.runescript.plugin.lang.psi.qualifiedName
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.ListSelectionModel

class RsChangeSignatureDialog(
    private val refactoringProject: Project,
    private val script: RsScript,
) : DialogWrapper(refactoringProject) {
    private val scriptName = codeField(script.qualifiedName, viewer = true)
    private val parameterModel = RsSignatureTableModel(script)
    private val parameterTable = createParameterTable()
    private val returns = codeField(RsChangeSignatureProcessor.returnEditorText(script))
    private val signaturePreview = codeField("", viewer = true, oneLine = false)

    init {
        title = "Change Signature"
        parameterModel.addTableModelListener { updateSignaturePreview() }
        returns.addDocumentListener(
            object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) = updateSignaturePreview()
            },
        )
        updateSignaturePreview()
        init()
        setOKButtonText("Refactor")
        if (parameterModel.rowCount > 0) parameterTable.setRowSelectionInterval(0, 0)
    }

    override fun createCenterPanel(): JComponent {
        val parameterPanel = createParameterPanel()
        val panel = JPanel(GridBagLayout())
        panel.add(JBLabel("Name:"), constraints(row = 0, column = 0, rightInset = 10))
        panel.add(scriptName, constraints(row = 0, column = 1, weightX = 1.0, fill = GridBagConstraints.HORIZONTAL))
        panel.add(JBLabel("Parameters:"), constraints(row = 1, column = 0, topInset = 12, gridWidth = 2))
        panel.add(
            parameterPanel,
            constraints(
                row = 2,
                column = 0,
                gridWidth = 2,
                weightX = 1.0,
                weightY = 1.0,
                fill = GridBagConstraints.BOTH,
            ),
        )
        panel.add(JBLabel("Return types:"), constraints(row = 3, column = 0, topInset = 12, rightInset = 10))
        panel.add(
            returns,
            constraints(
                row = 3,
                column = 1,
                topInset = 12,
                weightX = 1.0,
                fill = GridBagConstraints.HORIZONTAL,
            ),
        )
        panel.add(
            titledSeparator("Signature Preview"),
            constraints(row = 4, column = 0, topInset = 16, gridWidth = 2, weightX = 1.0, fill = GridBagConstraints.HORIZONTAL),
        )
        panel.add(
            signaturePreview,
            constraints(
                row = 5,
                column = 0,
                topInset = 6,
                gridWidth = 2,
                weightX = 1.0,
                weightY = 0.45,
                fill = GridBagConstraints.BOTH,
            ),
        )
        return panel.apply {
            preferredSize = JBUI.size(720, 500)
            minimumSize = JBUI.size(620, 420)
        }
    }

    override fun doOKAction() {
        stopEditing()
        setErrorText(null)
        when (val parsed = RsChangeSignatureProcessor.createChange(refactoringProject, script, parameterModel.parameters(), returns.text)) {
            is RsChangeSignatureProcessor.ParseResult.Error -> {
                setErrorText(parsed.message)
                return
            }

            is RsChangeSignatureProcessor.ParseResult.Success -> {
                val error = RsChangeSignatureProcessor.apply(refactoringProject, script, parsed.change)
                if (error != null) {
                    setErrorText(error)
                    return
                }
            }
        }
        super.doOKAction()
    }

    private fun createParameterTable(): JBTable =
        JBTable(parameterModel).apply {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            tableHeader = null
            showHorizontalLines = false
            showVerticalLines = false
            intercellSpacing = Dimension(0, 0)
            rowHeight = JBUI.scale(24)
            emptyText.text = "No parameters"
            preferredScrollableViewportSize = JBUI.size(680, 230)
            putClientProperty("terminateEditOnFocusLost", true)

            columnModel.getColumn(RsSignatureTableModel.TYPE_COLUMN).apply {
                preferredWidth = JBUI.scale(180)
                maxWidth = JBUI.scale(260)
                cellRenderer = SyntaxCellRenderer(RsSyntaxHighlighterColors.KEYWORD)
                cellEditor =
                    DefaultCellEditor(
                        JComboBox(
                            script.typeManager.typeKeywords
                                .map(CharSequence::toString)
                                .sorted()
                                .toTypedArray(),
                        ).apply { font = codeFont() },
                    )
            }
            columnModel.getColumn(RsSignatureTableModel.NAME_COLUMN).apply {
                preferredWidth = JBUI.scale(500)
                cellRenderer = SyntaxCellRenderer(RsSyntaxHighlighterColors.LOCAL_VARIABLE)
                cellEditor = DefaultCellEditor(JBTextField().apply { font = codeFont() })
            }
        }

    private fun createParameterPanel(): JComponent =
        ToolbarDecorator
            .createDecorator(parameterTable)
            .setAddAction {
                stopEditing()
                val row = parameterModel.addParameter()
                parameterTable.setRowSelectionInterval(row, row)
                parameterTable.editCellAt(row, RsSignatureTableModel.TYPE_COLUMN)
                parameterTable.editorComponent?.requestFocusInWindow()
            }.setRemoveAction {
                stopEditing()
                val selected = parameterTable.selectedRow
                parameterModel.removeParameter(selected)
                selectNearestRow(selected)
            }.setMoveUpAction {
                stopEditing()
                moveSelectedParameter(-1)
            }.setMoveDownAction {
                stopEditing()
                moveSelectedParameter(1)
            }.createPanel()

    private fun codeField(
        text: String,
        viewer: Boolean = false,
        oneLine: Boolean = true,
    ): EditorTextField =
        EditorTextField(text, refactoringProject, RsFileType).apply {
            setOneLineMode(oneLine)
            setViewer(viewer)
            setFontInheritedFromLAF(false)
            if (!oneLine) preferredSize = JBUI.size(680, 112)
        }

    private fun updateSignaturePreview() {
        signaturePreview.text = calculateSignaturePreview()
    }

    private fun calculateSignaturePreview(): String {
        val parameters = parameterModel.parameters().map { "${it.typeName} $${it.name}" }
        val returnTypes =
            returns.text
                .split(',', '\n')
                .map(String::trim)
                .filter(String::isNotEmpty)
        val includeParameters = script.parameterList != null || parameters.isNotEmpty() || returnTypes.isNotEmpty()
        val includeReturns = script.returnList != null || returnTypes.isNotEmpty()
        val oneLineParameters = parameters.joinToString(", ", prefix = "(", postfix = ")")
        val oneLineSignature =
            buildString {
                append(script.qualifiedName)
                if (includeParameters) append(oneLineParameters)
                if (includeReturns) append(returnTypes.joinToString(", ", prefix = "(", postfix = ")"))
            }
        if (oneLineSignature.length <= PREVIEW_WRAP_COLUMN || parameters.size <= 1) return oneLineSignature

        val indentation = " ".repeat(script.qualifiedName.length + 1)
        return buildString {
            append(script.qualifiedName)
            append('(')
            append(parameters.joinToString(",\n$indentation"))
            append(')')
            if (includeReturns) append(returnTypes.joinToString(", ", prefix = "(", postfix = ")"))
        }
    }

    private fun stopEditing() {
        parameterTable.cellEditor?.stopCellEditing()
    }

    private fun moveSelectedParameter(offset: Int) {
        val selected = parameterTable.selectedRow
        val destination = selected + offset
        if (selected !in 0 until parameterModel.rowCount || destination !in 0 until parameterModel.rowCount) return
        parameterModel.moveParameter(selected, destination)
        parameterTable.setRowSelectionInterval(destination, destination)
    }

    private fun selectNearestRow(previousSelection: Int) {
        if (parameterModel.rowCount == 0) return
        val row = previousSelection.coerceIn(0, parameterModel.rowCount - 1)
        parameterTable.setRowSelectionInterval(row, row)
    }

    private fun titledSeparator(text: String): JComponent = TitledSeparator(text)

    private fun codeFont() = EditorColorsManager.getInstance().globalScheme.getFont(EditorFontType.PLAIN)

    private fun constraints(
        row: Int,
        column: Int,
        gridWidth: Int = 1,
        weightX: Double = 0.0,
        weightY: Double = 0.0,
        fill: Int = GridBagConstraints.NONE,
        topInset: Int = 0,
        rightInset: Int = 0,
    ): GridBagConstraints =
        GridBagConstraints().apply {
            gridx = column
            gridy = row
            this.gridwidth = gridWidth
            this.weightx = weightX
            this.weighty = weightY
            this.fill = fill
            anchor = GridBagConstraints.WEST
            insets = Insets(JBUI.scale(topInset), 0, 0, JBUI.scale(rightInset))
        }

    private class SyntaxCellRenderer(
        private val attributesKey: com.intellij.openapi.editor.colors.TextAttributesKey,
    ) : ColoredTableCellRenderer() {
        override fun customizeCellRenderer(
            table: JTable,
            value: Any?,
            selected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int,
        ) {
            val scheme = EditorColorsManager.getInstance().globalScheme
            font = scheme.getFont(EditorFontType.PLAIN)
            val attributes =
                if (selected) {
                    SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES
                } else {
                    SimpleTextAttributes.fromTextAttributes(scheme.getAttributes(attributesKey))
                }
            append(value?.toString().orEmpty(), attributes)
            border = JBUI.Borders.emptyLeft(8)
        }
    }

    private companion object {
        const val PREVIEW_WRAP_COLUMN = 88
    }
}
