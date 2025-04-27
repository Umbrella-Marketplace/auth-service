package dev.kachvev.serverscript.repository

import dev.kachvev.serverscript.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByNicknameAndUniqueId(nickname: String, uniqueId: String): User?
    fun findByUniqueId(uniqueId: String): User?
    fun findByTelegramId(telegramId: String): User?

    fun findByLastLoginAfter(date: LocalDateTime): List<User>
}