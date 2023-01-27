package io.github.reugn.aerospike.cli

import java.sql.DriverManager
import java.sql.ResultSetMetaData

class AerospikeSession(
    host: String,
    port: Int,
    namespace: String,
    urlOpts: String
) : AutoCloseable {

    private val connection = run {
        val jdbcUrl = "jdbc:aerospike:$host:$port/$namespace?$urlOpts"
        Class.forName("com.aerospike.jdbc.AerospikeDriver")
        DriverManager.getConnection(jdbcUrl)
    }

    fun execute(query: String): List<List<String>> {
        val q = query.trim(';')
        if (q.equals("show tables", ignoreCase = true)) {
            return showTables()
        } else if (q.equals("show schemas", ignoreCase = true)) {
            return showSchemas()
        } else if (q.startsWith("describe ", ignoreCase = true)) {
            return describeTable(q.substring(9))
        } else if (isUpdate(query)) {
            return executeUpdate(q)
        } else if (isQuery(query)) {
            return executeQuery(q)
        }
        throw IllegalArgumentException("Unsupported query format")
    }

    private fun executeQuery(query: String): List<List<String>> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(query)
        val resultSetMetaData: ResultSetMetaData = resultSet.metaData

        val result = mutableListOf<List<String>>()
        val columnNames = (1..resultSetMetaData.columnCount)
            .map { resultSetMetaData.getColumnName(it) }
        result.add(columnNames)

        while (resultSet.next()) {
            val row = (0 until resultSetMetaData.columnCount)
                .map {
                    val columnName: String? = columnNames[it]
                    columnName?.let { s -> resultSet.getString(s) } ?: "null"
                }
                .toList()
            result.add(row)
        }
        resultSet?.close()
        statement?.close()
        return result
    }

    private fun executeUpdate(query: String): List<List<String>> {
        val statement = connection.createStatement()
        val rowCount = statement.executeUpdate(query)
        statement?.close()
        return listOf(listOf("Row Count"), listOf(rowCount.toString()))
    }

    private fun showTables(): List<List<String>> {
        val databaseMetaData = connection.metaData
        val resultSet = databaseMetaData.getTables(null, null, "%", null)
        val result = mutableListOf<List<String>>()
        result.add(listOf("Table"))
        while (resultSet.next()) {
            result.add(listOf(resultSet.getString(3)))
        }
        resultSet?.close()
        return result
    }

    private fun showSchemas(): List<List<String>> {
        val resultSet = connection.metaData.catalogs
        val result = mutableListOf<List<String>>()
        result.add(listOf("Schema"))
        while (resultSet.next()) {
            result.add(listOf(resultSet.getString(1)))
        }
        resultSet?.close()
        return result
    }

    private fun describeTable(tableName: String): List<List<String>> {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM $tableName LIMIT 1")
        val metaData = resultSet.metaData
        val result = mutableListOf<List<String>>()
        result.add(listOf("Column", "Type"))
        for (i in 1 until metaData.columnCount) {
            result.add(listOf(metaData.getColumnName(i), metaData.getColumnTypeName(i)))
        }
        resultSet?.close()
        statement?.close()
        return result
    }

    private fun isQuery(query: String): Boolean {
        return query.startsWith("select ", ignoreCase = true)
    }

    private fun isUpdate(query: String): Boolean {
        return query.startsWith("insert ", ignoreCase = true)
                || query.startsWith("update ", ignoreCase = true)
                || query.startsWith("delete ", ignoreCase = true)
                || query.startsWith("truncate ", ignoreCase = true)
    }

    override fun close() {
        connection?.close()
    }
}
