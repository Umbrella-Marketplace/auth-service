package dev.zornov.market.auth.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class TempKeyService {
    data class Key(
        val id: UUID = UUID.randomUUID(),
        val userId: String,
        val name: String,
        val validUntil: Instant = Instant.now().plusSeconds(600)
    )
    private val keys = mutableMapOf<UUID, Key>()

    fun createOrGet(userId: String, username: String): Key {
        val now = Instant.now()
        val existing = keys.values.find { it.userId == userId && it.validUntil.isAfter(now) }
        return existing ?: Key(userId = userId, name = username).also { keys[it.id] = it }
    }

    fun getKey(id: UUID): Key? = keys[id]

    @Scheduled(fixedRate = 10 * 60 * 1000)
    fun clearExpiredKeys() {
        val now = Instant.now()
        keys.entries.removeIf { it.value.validUntil.isBefore(now) }
    }
}
