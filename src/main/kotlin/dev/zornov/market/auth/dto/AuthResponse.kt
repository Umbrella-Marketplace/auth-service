package dev.zornov.market.auth.dto

import java.time.Instant
import java.util.*

sealed interface AuthResponse {
    val isAuthorized: Boolean

    data class AuthorizedResponse(
        val jwt: String
    ) : AuthResponse {
        override val isAuthorized: Boolean = true
    }

    data class UnauthorizedResponse(
        val tempKey: UUID,
        val validUntil: Instant
    ) : AuthResponse {
        override val isAuthorized: Boolean = false
    }
}