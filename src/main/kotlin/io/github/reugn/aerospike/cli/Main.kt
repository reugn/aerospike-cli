package io.github.reugn.aerospike.cli

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime

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
    val terminal = Terminal()

    val app = AerospikeCli(aerospikeSession, terminal)

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            app.close()
        }
    })

    app.run()
    exitProcess(0)
}

fun showVersionAndExit() {
    println("${BuildConfig.APP_NAME}: ${BuildConfig.APP_VERSION}")
    exitProcess(0)
}
