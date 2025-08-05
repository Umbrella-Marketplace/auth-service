package dev.zornov.market.auth.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class TempKeyService {
    data class Key(
        val id: UUID,
        val name: String,
        val userId: String,
        val validUntil: Instant = Instant.now().plusSeconds(600)
    )

    private val tempKeys = mutableMapOf<UUID, Key>()

    fun generate(name: String, userId: String): Key {
        val key = Key(UUID.randomUUID(), name, userId)
        tempKeys[key.id] = key
        return key
    }

    fun getKey(keyId: UUID): Key? = tempKeys[keyId]

    @Scheduled(fixedRate = 60 * 1000)
    fun clearExpiredKeys() {
        tempKeys.forEach { (_, key) ->
            if (key.validUntil < Instant.now()) {
                tempKeys.remove(key.id)
            }
        }
    }

}