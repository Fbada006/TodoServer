package com.disruption.repository


import com.disruption.models.Todo
import com.disruption.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

class TodoRepository : Repository {
    override suspend fun addUser(
        email: String,
        displayName: String,
        passwordHash: String
    ): User? {
        var statement: InsertStatement<Number>? = null // 1
        dbQuery { // 2
            // 3
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }
        // 4
        return rowToUser(statement?.resultedValues?.get(0))
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }

    override suspend fun findUser(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        }
        return statement?.resultedValues?.get(0).rowToTodo()
    }

    override suspend fun getTodos(userId: Int): List<Todo> {
        return dbQuery {
            Todos.select {
                Todos.userId.eq(userId) // 3
            }.mapNotNull { it.rowToTodo() }
        }
    }

    override suspend fun getTodoById(todoId: Int): Todo? {
        return dbQuery {
            Todos.select {
                Todos.id.eq(todoId)
            }.singleOrNull().rowToTodo()
        }
    }

    private fun ResultRow?.rowToTodo(): Todo? {
        if (this == null) {
            return null
        }
        return Todo(
            id = this[Todos.id],
            userId = this[Todos.userId],
            todo = this[Todos.todo],
            done = this[Todos.done],
            createdAt = this[Todos.createdAt]
        )
    }

    private suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}
