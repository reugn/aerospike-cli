# aerospike-cli
[![Build](https://github.com/reugn/aerospike-cli/actions/workflows/build.yml/badge.svg)](https://github.com/reugn/aerospike-cli/actions/workflows/build.yml)

A command line utility to query the Aerospike database using SQL.  
This tool wraps the [Aerospike JDBC driver](https://github.com/aerospike/aerospike-jdbc) and is written in Kotlin.

## Prerequisites
* Java 8

## Installation
* Build from source
```
./gradlew clean build
```
* Download an executable jar from the [releases](https://github.com/reugn/aerospike-cli/releases)

## Usage
```
Usage: aerospike-cli options_list
Options: 
    --host, -host [localhost] -> Aerospike cluster hostname { String }
    --port, -port [3000] -> Aerospike cluster port { Int }
    --namespace, -ns [] -> Aerospike namespace { String }
    --options, -opts [] -> JDBC driver configuration options as a URL query string { String }
    --version, -v [false] -> Show application version and exit 
    --help, -h -> Usage info 
```
To run the application with the default values
```
java -jar build/libs/aerospike-cli-<version>-standalone.jar
```
Run specifying the namespace
```
java -jar build/libs/aerospike-cli-<version>-standalone.jar -ns test
```
Read more about optional [configuration parameters](https://github.com/aerospike/aerospike-jdbc/blob/main/docs/params.md).

## Supported SQL Statements
See the [Aerospike JDBC Supported Statements](https://github.com/aerospike/aerospike-jdbc/blob/main/docs/examples.md)
page for query examples.

### Additional commands
* SHOW SCHEMAS
* SHOW TABLES
* DESCRIBE *table_name*

## License
Licensed under the [Apache 2.0 License](./LICENSE).
