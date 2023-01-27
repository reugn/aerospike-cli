package io.github.reugn.aerospike.cli

object Format {

    @Suppress("kotlin:S3776")
    fun List<List<String>>.toTable(): String {
        val maxLengths = findMaxLengths(this)
        val sb = StringBuilder()
        for (i in this.indices) {
            for (j in this[i].indices) {
                val currentValue: String = this[i][j]
                sb.append(' ')
                sb.append(currentValue)
                for (k in 0 until maxLengths[j] - currentValue.length + 1) {
                    sb.append(' ')
                }
                if (j < this[i].size - 1) sb.append("|")
            }
            if (i == 0) {
                sb.append('\n')
                for (j in this[i].indices) {
                    sb.append("-".repeat(maxLengths[j] + 2))
                    if (j < this[i].size - 1) sb.append("+")
                }
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    private fun findMaxLengths(resultSet: List<List<String>>): List<Int> {
        val maxLengths = mutableListOf<Int>()
        for (i in resultSet[0].indices) {
            var maxLength = 0
            for (j in resultSet.indices) {
                if (resultSet[j][i].length > maxLength) {
                    maxLength = resultSet[j][i].length
                }
            }
            maxLengths.add(maxLength)
        }
        return maxLengths
    }

    fun List<List<String>>.rowsNumber(): String {
        val rows = this.size - 1
        val rowsString = if (rows == 1) "row" else "rows"
        return "($rows $rowsString)"
    }
}
