package com.disruption.routes

import com.disruption.API_VERSION
import com.disruption.auth.MySession
import com.disruption.repository.Repository
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.locations.*
import io.ktor.server.locations.post
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

const val TODOS = "$API_VERSION/todos"
const val TODO_DETAILS = "$API_VERSION/tododetails"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class TodoRoute

@KtorExperimentalLocationsAPI
@Location(TODO_DETAILS)
class TodoDetailsRoute

@KtorExperimentalLocationsAPI
fun Route.todos(db: Repository) {
    authenticate("auth-jwt") {
        post<TodoRoute> {
            val todosParameters = call.receive<HashMap<String, String>>()
            val todo = todosParameters["todo"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing todo data. Please add todo information."
                )
            val done = todosParameters["done"] ?: "false"

            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving user. Does this user exist?"
                )
                return@post
            }

            try {
                val currentTodo = db.addTodo(
                    user.userId, todo, done.toBoolean()
                )
                currentTodo?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTodo)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems saving todo. Please try again later.")
            }
        }

        get<TodoRoute> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving user")
                return@get
            }
            try {
                val todos = db.getTodos(user.userId)
                call.respond(todos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Todos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
            }
        }

        get<TodoDetailsRoute> {
            val todoId = call.request.queryParameters["todoid"]?.toInt()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "The todo id cannot be missing in this request."
                )

            val todo = db.getTodoById(todoId)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound, "Problems retrieving todo. Please try again"
                )

            return@get call.respond(
                HttpStatusCode.OK, todo
            )
        }
    }
}

