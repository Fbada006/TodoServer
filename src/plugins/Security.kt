package com.disruption.plugins

import com.disruption.auth.JwtService
import com.disruption.auth.MySession
import com.disruption.repository.TodoRepository
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.sessions.*

fun Application.configureSecurity(db:TodoRepository, jwtService: JwtService) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    authentication {
        jwt("auth-jwt") {
            verifier(jwtService.verifier)
            realm = "Todo Server"
            validate { jwtCredential ->
                val payload = jwtCredential.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user= db.findUser(claimString)
                user
            }
        }
    }
}