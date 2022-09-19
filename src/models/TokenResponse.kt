package com.disruption.models

import java.io.Serializable

data class TokenResponse(
    val token: String,
    val userId: Int
) : Serializable
