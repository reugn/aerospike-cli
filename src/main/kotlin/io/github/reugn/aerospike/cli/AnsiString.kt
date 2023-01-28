package io.github.reugn.aerospike.cli

import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle

object AnsiString {

    fun String.coloredBright(): String {
        return AttributedString(
            this,
            AttributedStyle.DEFAULT.foreground(AttributedStyle.BRIGHT)
        ).toAnsi()
    }

    fun String.coloredError(): String {
        return AttributedString(
            this,
            AttributedStyle.DEFAULT.foreground(114, 47, 55)
        ).toAnsi()
    }
}
