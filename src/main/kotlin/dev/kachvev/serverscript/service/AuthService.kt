package dev.kachvev.serverscript.service

import dev.kachvev.serverscript.ext.nowMoscow
import dev.kachvev.serverscript.model.User
import dev.kachvev.serverscript.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthService(
    val userRepository: UserRepository,
    val tokenService: TokenService
) {

    fun login(nickname: String, uniqueId: String): User {
        val user = userRepository.findByNicknameAndUniqueId(nickname, uniqueId)
        return if (user != null) {
            user.lastLogin = nowMoscow()
            userRepository.save(user)
        } else {
            userRepository.save(
                User(
                    nickname = nickname,
                    uniqueId = uniqueId,
                    subscriptionUntil = LocalDateTime.now().plusDays(30)
                )
            )
        }
    }

    fun linkTelegramByToken(token: String, telegramId: Long): Boolean {
        val uniqueId = tokenService.verifyToken(token) ?: return false
        val user = userRepository.findByUniqueId(uniqueId) ?: return false

        user.telegramId = telegramId.toString()
        userRepository.save(user)
        return true
    }

    fun findUserByTelegramId(telegramId: String): User? {
        return userRepository.findByTelegramId(telegramId)
    }

    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    fun findUsersLoggedInToday(): List<User> {
        val startOfDay = nowMoscow().toLocalDate().atStartOfDay()
        return userRepository.findByLastLoginAfter(startOfDay)
    }

}
