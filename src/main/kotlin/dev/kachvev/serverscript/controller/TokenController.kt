package dev.kachvev.serverscript.controller

import dev.kachvev.serverscript.dto.TokenRequest
import dev.kachvev.serverscript.repository.UserRepository
import dev.kachvev.serverscript.service.TelegramService
import dev.kachvev.serverscript.service.TokenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    val tokenService: TokenService,
    val userRepository: UserRepository,
    val telegramService: TelegramService
) {
    @PostMapping("/api/generate-token")
    fun generateToken(@RequestBody request: TokenRequest): Map<String, String> {
        val token = tokenService.generateToken(request.uniqueId)
        return mapOf(
            "token" to token,
            "uniqueId" to request.uniqueId
        )
    }

    @PostMapping("/api/unlink-telegram")
    fun unlinkTelegram(@RequestBody request: TokenRequest): ResponseEntity<Void> {
        val success = tokenService.unlinkTelegram(request.uniqueId)

        val telegramId = userRepository.findByUniqueId(request.uniqueId)?.telegramId
        if (telegramId != null) {
            telegramService.sendMessage(telegramId, "✅ Ваш аккаунт успешно отвязан от Telegram.")
        }

        return if (success) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
    }

}
