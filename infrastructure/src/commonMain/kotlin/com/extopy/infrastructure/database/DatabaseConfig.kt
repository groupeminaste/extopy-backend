package com.extopy.infrastructure.database

/** Connection settings for the database. */
data class DatabaseConfig(
    val protocol: String,
    val host: String,
    val name: String,
    val user: String,
    val password: String,
    val maximumPoolSize: Int = 10,
)
