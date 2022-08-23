package com.disruption

import com.disruption.auth.JwtService
import com.disruption.plugins.configureRouting
import com.disruption.plugins.configureSecurity
import com.disruption.plugins.configureSerialization
import com.disruption.repository.DatabaseFactory
import com.disruption.repository.TodoRepository
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val db = TodoRepository()
    val jwtService = JwtService()

    DatabaseFactory.init()
    configureSecurity(db, jwtService)
    configureRouting(db, jwtService)
    configureSerialization()
}

const val API_VERSION = "/v1"


