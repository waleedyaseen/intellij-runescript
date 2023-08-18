package io.runescript.plugin.ide.execution.build

import com.intellij.build.FilePosition
import com.intellij.build.events.BuildEvent
import com.intellij.build.events.MessageEvent
import com.intellij.build.events.impl.FileMessageEventImpl
import com.intellij.build.events.impl.MessageEventImpl
import com.intellij.build.output.BuildOutputInstantReader
import com.intellij.build.output.BuildOutputParser
import java.io.File
import java.util.function.Consumer

class RsBuildOutputParser(private val instance: RsBuildInstance) : BuildOutputParser {

    private val fileMessageContext = FileMessageContext()
    private val detailsBuilder = StringBuilder()
    private var detailsCount = 0
    private var collectingStackTrace = false

    override fun parse(
        line: String,
        reader: BuildOutputInstantReader,
        messageConsumer: Consumer<in BuildEvent>
    ): Boolean {
        if (detailsCount > 0) {
            detailsCount--
            detailsBuilder.appendLine(line)
            if (detailsCount == 0) {
                val filePath = File(fileMessageContext.path)
                val filePosition = FilePosition(filePath, fileMessageContext.line, fileMessageContext.column)
                val fileMessage = FileMessageEventImpl(
                    instance.buildId,
                    MessageEvent.Kind.ERROR,
                    "Compiler Errors",
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
        val syntaxErrorMatch = ERROR_PATTERN.matchEntire(line)
        if (syntaxErrorMatch != null) {
            fileMessageContext.path = syntaxErrorMatch.groups[1]!!.value
            fileMessageContext.line = syntaxErrorMatch.groups[2]!!.value.toInt() - 1
            fileMessageContext.column = syntaxErrorMatch.groups[3]!!.value.toInt() - 1
            fileMessageContext.message = syntaxErrorMatch.groups[4]!!.value
            detailsBuilder.appendLine(fileMessageContext.message)
            detailsCount = 2
            return true
        }
        if (collectingStackTrace) {
            if (line.startsWith("Process finished")) {
                val fileMessage = MessageEventImpl(
                    instance.buildId,
                    MessageEvent.Kind.ERROR,
                    "Compiler Errors",
                    "Internal Error",
                    detailsBuilder.toString()
                )
                messageConsumer.accept(fileMessage)
                collectingStackTrace = false
            } else {
                detailsBuilder.appendLine(line)
            }
        } else if (line.startsWith(EXCEPTION_BEGINNING)) {
            detailsBuilder.setLength(0)
            val exceptionLine = line.substring(line.indexOf('"', EXCEPTION_BEGINNING.length) + 2)
            detailsBuilder.appendLine(exceptionLine)
            collectingStackTrace = true
        }
        return false
    }

    companion object {
        private val ERROR_PATTERN =
            Regex("(?<path>.+):(?<line>\\d+):(?<column>\\d+): (?:ERROR|SYNTAX_ERROR): (?<message>[^\\r\\n]+)")
        private const val EXCEPTION_BEGINNING = "Exception in thread \""
    }
}

private data class FileMessageContext(
    var path: String = "",
    var line: Int = 0,
    var column: Int = 0,
    var message: String = ""
)