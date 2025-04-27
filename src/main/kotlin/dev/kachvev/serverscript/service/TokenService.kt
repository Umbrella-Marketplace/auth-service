package dev.kachvev.serverscript.service

import dev.kachvev.serverscript.model.TemporaryToken
import dev.kachvev.serverscript.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class TokenService(
    val userRepository: UserRepository
) {
    val tokens = ConcurrentHashMap<String, TemporaryToken>()

    fun generateToken(uniqueId: String): String {
        val token = UUID.randomUUID().toString().replace("-", "")
        tokens[token] = TemporaryToken(
            uniqueId = uniqueId,
            createdAt = Instant.now()
        )
        return token
    }

    fun verifyToken(token: String): String? {
        val tempToken = tokens[token] ?: return null

        if (Instant.now().epochSecond - tempToken.createdAt.epochSecond > 300) { // 5 минут
            tokens.remove(token)
            return null
        }

        tokens.remove(token)
        return tempToken.uniqueId
    }

    fun unlinkTelegram(uniqueId: String): Boolean {
        val user = userRepository.findByUniqueId(uniqueId) ?: return false

        if (user.telegramId == null) {
            return false
        }

        user.telegramId = null
        userRepository.save(user)
        return true
    }

    @Scheduled(fixedRate = 60_000)
    fun cleanExpiredTokens() {
        val now = Instant.now().epochSecond
        tokens.entries.removeIf { (_, tempToken) ->
            now - tempToken.createdAt.epochSecond > 300
        }
    }
}
