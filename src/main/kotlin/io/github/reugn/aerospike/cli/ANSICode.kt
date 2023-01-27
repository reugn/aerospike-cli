package io.github.reugn.aerospike.cli

object ANSICode {
    private const val RESET = 0

    const val HIGH_INTENSITY = 1
    const val LOW_INTENSITY = 2

    const val BACKGROUND_SHIFT = 10
    const val BRIGHT_SHIFT = 60

    const val ITALIC = 3
    const val UNDERLINE = 4
    const val BLINK = 5
    const val REVERSE = 7
    const val HIDDEN = 8
    const val STRIKE = 9

    const val BLACK = 30
    const val RED = 31
    const val GREEN = 32
    const val YELLOW = 33
    const val BLUE = 34
    const val PURPLE = 35
    const val CYAN = 36
    const val WHITE = 37

    const val BRIGHT_BLACK = BLACK + BRIGHT_SHIFT
    const val BRIGHT_RED = RED + BRIGHT_SHIFT
    const val BRIGHT_GREEN = GREEN + BRIGHT_SHIFT
    const val BRIGHT_YELLOW = YELLOW + BRIGHT_SHIFT
    const val BRIGHT_BLUE = BLUE + BRIGHT_SHIFT
    const val BRIGHT_PURPLE = PURPLE + BRIGHT_SHIFT
    const val BRIGHT_CYAN = CYAN + BRIGHT_SHIFT
    const val BRIGHT_WHITE = WHITE + BRIGHT_SHIFT

    fun String.applyCodes(vararg codes: Int) = "\u001B[${RESET}m".let { reset ->
        val tags = codes.joinToString { "\u001B[${it}m" }
        split(reset).filter { it.isNotEmpty() }.joinToString(separator = "") { tags + it + reset }
    }
}
