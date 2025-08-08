package dev.zornov.market.auth.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TempKeyService {

    companion object {
        private const val TTL_SECONDS = 600L
    }

    data class Key(
        val id: UUID = UUID.randomUUID(),
        val userId: String,
        val name: String,
        val validUntil: Instant = Instant.now().plusSeconds(TTL_SECONDS)
    ) {
        fun validUntilTimestamp(): Long = validUntil.epochSecond
        fun isExpired(): Boolean = Instant.now().isAfter(validUntil)
    }

    private val keysById = ConcurrentHashMap<UUID, Key>()
    private val keysByUserId = ConcurrentHashMap<String, Key>()

    fun createOrGet(userId: String, username: String): Key {
        val existingKey = keysByUserId[userId]
        if (existingKey != null && !existingKey.isExpired()) {
            return existingKey
        }

        val newKey = Key(userId = userId, name = username)
        keysById[newKey.id] = newKey
        keysByUserId[userId] = newKey

        return newKey
    }

    fun getKey(id: UUID): Key? = keysById[id]?.takeUnless { it.isExpired() }

    @Scheduled(fixedRate = TTL_SECONDS * 1000)
    fun clearExpiredKeys() {
        val expiredIds = keysById.filterValues { it.isExpired() }.keys

        expiredIds.forEach { id ->
            keysById.remove(id)?.let { key ->
                keysByUserId.remove(key.userId)
            }
        }
    }
}
