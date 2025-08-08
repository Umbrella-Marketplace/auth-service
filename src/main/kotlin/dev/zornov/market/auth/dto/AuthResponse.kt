package dev.zornov.market.auth.dto

import java.util.*

sealed interface AuthResponse {
    val isAuthorized: Int

    data class AuthorizedResponse(
        val jwt: String
    ) : AuthResponse {
        override val isAuthorized: Int = 1
    }

    data class UnauthorizedResponse(
        val tempKey: UUID,
        val validUntil: Long
    ) : AuthResponse {
        override val isAuthorized: Int = 0
    }

    data class WaitApproveResponse(
        val message: String
    ) : AuthResponse {
        override val isAuthorized = -1
    }
}