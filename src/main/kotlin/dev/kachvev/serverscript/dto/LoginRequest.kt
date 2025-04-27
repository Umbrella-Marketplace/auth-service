package dev.kachvev.serverscript.dto

data class LoginRequest(
    val nickname: String,
    val uniqueId: String
)