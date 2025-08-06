package dev.zornov.market.auth.controller

import dev.zornov.market.auth.dto.AuthRequest
import dev.zornov.market.auth.dto.AuthResponse
import dev.zornov.market.auth.repository.UserRepository
import dev.zornov.market.auth.service.AuthService
import dev.zornov.market.auth.service.TempKeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    val userRepository: UserRepository,
    val tempKeyService: TempKeyService,
    val authService: AuthService
) {
    @PostMapping
    fun auth(@RequestBody req: AuthRequest): ResponseEntity<AuthResponse> {
        val userOpt = userRepository.findById(req.id)
        println("Request")

        return if (userOpt.isPresent) {
            val jwt = authService.issueJwt(userOpt.get())
            ResponseEntity.ok(AuthResponse.AuthorizedResponse(jwt = jwt))
        } else {
            val key = tempKeyService.createOrGet(req.id, req.name)
            ResponseEntity.ok(AuthResponse.UnauthorizedResponse(tempKey = key.id, validUntil = key.validUntil))
        }
    }
}