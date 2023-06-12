package io.runescript.plugin.ide.execution.build

import com.intellij.build.FilePosition
import com.intellij.build.events.BuildEvent
import com.intellij.build.events.MessageEvent
import com.intellij.build.events.impl.FileMessageEventImpl
import com.intellij.build.output.BuildOutputInstantReader
import com.intellij.build.output.BuildOutputParser
import java.io.File
import java.util.function.Consumer

class RsBuildOutputParser(private val instance: RsBuildInstance) : BuildOutputParser {

    private val fileMessageContext = FileMessageContext()
    private val detailsBuilder = StringBuilder()
    private var parsingSyntaxDetailsCount = 0

    override fun parse(line: String, reader: BuildOutputInstantReader, messageConsumer: Consumer<in BuildEvent>): Boolean {
        if (parsingSyntaxDetailsCount > 0) {
            parsingSyntaxDetailsCount--
            detailsBuilder.appendLine(line)
            if (parsingSyntaxDetailsCount == 0) {
                val filePath = File(fileMessageContext.path)
                val filePosition = FilePosition(filePath, fileMessageContext.line, fileMessageContext.column)
                val fileMessage = FileMessageEventImpl(instance.buildId,
                    MessageEvent.Kind.ERROR,
                    "Syntax Errors",
                    fileMessageContext.message,
                    detailsBuilder.toString(),
                    filePosition
                )
                instance.errorCount.incrementAndGet()
                messageConsumer.accept(fileMessage)
                detailsBuilder.setLength(0)
            }
            return true
        }
        val syntaxErrorMatch = SYNTAX_ERROR_PATTERN.matchEntire(line)
        if (syntaxErrorMatch != null) {
            fileMessageContext.path = syntaxErrorMatch.groups["path"]!!.value
            fileMessageContext.line = syntaxErrorMatch.groups["line"]!!.value.toInt() - 1
            fileMessageContext.column = syntaxErrorMatch.groups["column"]!!.value.toInt() - 1
            fileMessageContext.message = syntaxErrorMatch.groups["message"]!!.value
            detailsBuilder.appendLine(fileMessageContext.message)
            parsingSyntaxDetailsCount = 2
            return true
        }
        return false
    }

    companion object {
        private val SYNTAX_ERROR_PATTERN = Regex("(?<path>.+):(?<line>\\d+):(?<column>\\d+): SYNTAX_ERROR: (?<message>[^\\r\\n]+)")
    }
}

private data class FileMessageContext(var path: String = "", var line: Int = 0, var column: Int = 0, var message: String = "")
