package com.disruption.plugins

import com.disruption.auth.JwtService
import com.disruption.auth.hash
import com.disruption.repository.TodoRepository
import com.disruption.routes.todos
import com.disruption.routes.users
import io.ktor.server.application.*
import io.ktor.server.locations.*
import io.ktor.server.routing.*

@KtorExperimentalLocationsAPI
fun Application.configureRouting(db: TodoRepository, jwtService: JwtService) {
    install(Locations) {}
    val hashFunction = { s: String -> hash(s) }

    routing {
        users(db = db, jwtService = jwtService, hashFunction = hashFunction)
        todos(db = db)
    }
}