package com.disruption

import com.disruption.auth.JwtService
import com.disruption.auth.MySession
import com.disruption.plugins.configureRouting
import com.disruption.plugins.configureSecurity
import com.disruption.plugins.configureSerialization
import com.disruption.repository.DatabaseFactory
import com.disruption.repository.TodoRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*
import io.ktor.server.sessions.*
import kotlin.collections.set

@KtorExperimentalLocationsAPI
fun main() {
    DatabaseFactory.init()
    val db = TodoRepository()
    val jwtService = JwtService()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSecurity(db, jwtService)
        configureRouting(db, jwtService)
        configureSerialization()
    }.start(wait = true)
}

const val API_VERSION = "/v1"


