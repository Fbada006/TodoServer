package com.disruption

import com.disruption.auth.JwtService
import com.disruption.plugins.configureRouting
import com.disruption.plugins.configureSecurity
import com.disruption.plugins.configureSerialization
import com.disruption.repository.DatabaseFactory
import com.disruption.repository.TodoRepository
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*

@KtorExperimentalLocationsAPI
fun main() {

    val db = TodoRepository()
    val jwtService = JwtService()

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
    DatabaseFactory.init()
        configureSecurity(db, jwtService)
        configureRouting(db, jwtService)
        configureSerialization()
    }.start(wait = true)
}

const val API_VERSION = "/v1"


