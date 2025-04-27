package dev.kachvev.serverscript.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("users")
data class User(
    @Id val id: String? = null,
    val nickname: String,
    val uniqueId: String,
    var subscriptionUntil: LocalDateTime,
    var telegramId: String? = null,
    var lastLogin: LocalDateTime = LocalDateTime.now(),
    var accessibleScripts: MutableList<String> = mutableListOf()
) {
    fun isSubscriptionActive(): Boolean = LocalDateTime.now().isBefore(subscriptionUntil)
}
