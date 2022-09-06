package com.disruption.routes

import com.disruption.API_VERSION
import com.disruption.auth.JwtService
import com.disruption.auth.MySession
import com.disruption.models.TokenResponse
import com.disruption.repository.Repository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*


const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_DETAILS = "$USERS/getuser"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
@Location(USER_DETAILS)
class UserDetailsRoute

@KtorExperimentalLocationsAPI
// 1
fun Route.users(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val signupParameters = call.receive<HashMap<String, String>>()
        val password = signupParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields: password"
            )
        val displayName = signupParameters["displayName"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields: displayName"
            )
        val email = signupParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields: email"
            )
        val hash = hashFunction(password)
        try {
            val newUser = db.addUser(email, displayName, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respond(
                    status = HttpStatusCode.Created,
                    TokenResponse(
                        token = jwtService.generateToken(newUser)
                    )
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }

    post<UserLoginRoute> { // 1
        val signinParameters = call.receive<HashMap<String, String>>()
        val password = signinParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields: Password"
            )
        val email = signinParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields: Email"
            )
        val hash = hashFunction(password)
        try {
            val currentUser = db.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    call.respond(
                        TokenResponse(
                            token = jwtService.generateToken(currentUser)
                        )
                    )
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest, "Problems retrieving User"
                    )
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }

    authenticate("auth-jwt") {
        get<UserDetailsRoute> {
            val userId = call.request.queryParameters["userid"]?.toInt()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "The user id cannot be missing in this request."
                )

            val user = db.findUser(userId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound, "Problems retrieving user. Does this user exist?"
                )

            return@get call.respond(
                HttpStatusCode.OK, user
            )
        }
    }
}

