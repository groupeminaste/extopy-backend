package com.extopy.infrastructure.database

/** Connection settings for the MySQL database. */
data class DatabaseConfig(
    val host: String,
    val port: Int = 3306,
    val name: String,
    val user: String,
    val password: String,
    val maximumPoolSize: Int = 10,
) {

    /** The JDBC URL of the database. */
    val jdbcUrl: String get() = "jdbc:mysql://$host:$port/$name"

}
