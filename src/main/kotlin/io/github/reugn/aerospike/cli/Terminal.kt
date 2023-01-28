package io.github.reugn.aerospike.cli

import io.github.reugn.aerospike.cli.AnsiString.coloredBright
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.TerminalBuilder
import java.io.PrintWriter

class Terminal : AutoCloseable {

    private val terminal = TerminalBuilder.builder()
        .system(true)
        .build()

    private val reader: LineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .variable(LineReader.BLINK_MATCHING_PAREN, 0)
        .option(LineReader.Option.HISTORY_IGNORE_SPACE, true)
        .build()

    private val writer: PrintWriter = terminal.writer()

    fun readLine(prompt: String): String? {
        return reader.readLine(prompt.coloredBright(), null, null)
    }

    fun writeLine(line: String?) {
        writer.println(line)
        writer.flush()
    }

    override fun close() {
        terminal.close()
    }
}
