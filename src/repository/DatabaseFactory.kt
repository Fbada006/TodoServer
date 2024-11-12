package com.disruption.repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari()) // 1

        // 2
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Todos)
        }
    }

    private fun hikari(): HikariDataSource {
        println("Database URL--------: ${System.getenv("DATABASE_URL")}")
        println("JDBC DRIVER-------: ${System.getenv("JDBC_DRIVER")}")

        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER") // 1
        // Parse the PostgreSQL URL
        val rawDatabaseUrl = System.getenv("DATABASE_URL") ?: error("DATABASE_URL not set")
        val jdbcUrl = if (rawDatabaseUrl.startsWith("jdbc:")) {
            rawDatabaseUrl
        } else {
            // Convert PostgreSQL URL to JDBC URL format
            val regex = "postgresql://(.*?):(.*?)@(.*?):(\\d+)/(.*?)$".toRegex()
            val matchResult = regex.find(rawDatabaseUrl) ?: error("Invalid DATABASE_URL format")
            val (username, password, host, port, database) = matchResult.destructured

            // Set credentials separately
            config.username = username
            config.password = password

            "jdbc:postgresql://$host:$port/$database"
        }

        config.jdbcUrl = jdbcUrl
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}

