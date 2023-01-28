package io.github.reugn.aerospike.cli

import io.github.reugn.aerospike.cli.AnsiString.coloredError
import io.github.reugn.aerospike.cli.Format.rowsNumber
import io.github.reugn.aerospike.cli.Format.toTable
import org.jline.reader.UserInterruptException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class AerospikeCli(
    private val aerospikeSession: AerospikeSession,
    private val terminal: Terminal
) : Runnable, AutoCloseable {

    @ExperimentalTime
    override fun run() {
        val prompt = "${aerospikeSession.host}:${aerospikeSession.port}/${aerospikeSession.namespace}> "
        do {
            val cmd = try {
                terminal.readLine(prompt)?.trim() ?: ""
            } catch (e: UserInterruptException) {
                break
            }
            if (cmd.isNotEmpty() && !exitCommand(cmd)) {
                val result = try {
                    val (resultSet: List<List<String>>, duration: Duration) = measureTimedValue {
                        aerospikeSession.execute(cmd)
                    }
                    StringBuilder()
                        .append(resultSet.toTable())
                        .appendLine()
                        .append(resultSet.rowsNumber())
                        .append(" ${duration.inWholeMicroseconds / 1000.0}ms")
                        .toString()
                } catch (e: Exception) {
                    e.message?.coloredError()
                }
                terminal.writeLine(result)
            }
        } while (!exitCommand(cmd))
    }

    override fun close() {
        terminal.writeLine("Exiting ${BuildConfig.APP_NAME}...")
        aerospikeSession.close()
        terminal.close()
    }

    private fun exitCommand(cmd: String): Boolean {
        return cmd.equals("quit", ignoreCase = true)
                || cmd.equals("exit", ignoreCase = true)
    }
}
