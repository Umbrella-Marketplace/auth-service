package dev.zornov.market.auth.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class User(
    @Id val id: String,
    val name: String,
    val roles: List<Role> = listOf(Role.USER)
)
