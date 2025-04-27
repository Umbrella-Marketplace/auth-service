package dev.kachvev.serverscript.model

import java.time.Instant

data class TemporaryToken(
    val uniqueId: String,
    val createdAt: Instant
)