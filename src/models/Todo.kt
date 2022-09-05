package com.disruption.models

data class Todo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val done: Boolean,
    val createdAt: Long,
    val dueAt: Long
)
