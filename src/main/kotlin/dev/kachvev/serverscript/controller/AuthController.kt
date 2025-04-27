package dev.kachvev.serverscript.controller

import dev.kachvev.serverscript.dto.LoginRequest
import dev.kachvev.serverscript.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController(
    val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Map<String, Any?>> {
        val user = authService.login(request.nickname, request.uniqueId)

        val profile = mapOf(
            "id" to user.id,
            "nickname" to user.nickname,
            "uniqueId" to user.uniqueId,
            "subscriptionUntil" to user.subscriptionUntil,
            "telegramId" to user.telegramId,
            "scripts" to user.accessibleScripts
        )

        return ResponseEntity.ok(profile)
    }
}