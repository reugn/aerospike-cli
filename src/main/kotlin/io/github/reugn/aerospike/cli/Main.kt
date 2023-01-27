package io.github.reugn.aerospike.cli

import io.github.reugn.aerospike.cli.ANSICode.BRIGHT_BLACK
import io.github.reugn.aerospike.cli.ANSICode.BRIGHT_RED
import io.github.reugn.aerospike.cli.ANSICode.applyCodes
import io.github.reugn.aerospike.cli.Format.rowsNumber
import io.github.reugn.aerospike.cli.Format.toTable
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.system.exitProcess
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@ExperimentalTime
fun main(args: Array<String>) {
    LogManager.getLogManager().reset()
    val globalLogger: Logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
    globalLogger.level = Level.OFF

    val parser = ArgParser(BuildConfig.APP_NAME)
    val host by parser.option(ArgType.String, shortName = "host", description = "Aerospike cluster hostname")
        .default("localhost")
    val port by parser.option(ArgType.Int, shortName = "port", description = "Aerospike cluster port")
        .default(3000)
    val namespace by parser.option(ArgType.String, shortName = "ns", description = "Aerospike namespace")
        .default("")
    val options by parser.option(
        ArgType.String, shortName = "opts",
        description = "JDBC driver configuration options as a URL query string"
    ).default("")
    val version by parser.option(
        ArgType.Boolean, fullName = "version", shortName = "v",
        description = "Show application version and exit"
    ).default(false)
    parser.parse(args)

    if (version) showVersionAndExit()

    val aerospikeSession = AerospikeSession(host, port, namespace, options)

    do {
        print("$host:$port/$namespace> ".applyCodes(BRIGHT_BLACK))
        val cmd = readln().trim()
        if (cmd.isNotEmpty() && !exitCommand(cmd)) {
            val result = try {
                val (resultSet: List<List<String>>, duration: Duration) = measureTimedValue {
                    aerospikeSession.execute(cmd)
                }
                "${resultSet.toTable()}\n${resultSet.rowsNumber()} ${duration.inWholeMicroseconds / 1000.0}ms"
            } catch (e: Exception) {
                e.message?.applyCodes(BRIGHT_RED)
            }
            println(result)
        }
    } while (!exitCommand(cmd))

    println("Exiting ${BuildConfig.APP_NAME}...")
    aerospikeSession.close()
    exitProcess(0)
}

fun exitCommand(cmd: String): Boolean {
    return cmd.equals("quit", ignoreCase = true)
            || cmd.equals("exit", ignoreCase = true)
}

fun showVersionAndExit() {
    println("${BuildConfig.APP_NAME}: ${BuildConfig.APP_VERSION}")
    exitProcess(0)
}
