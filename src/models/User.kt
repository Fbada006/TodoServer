package com.disruption.models

import io.ktor.server.auth.*
import java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal
