package dev.zornov.market.auth.controller

import dev.zornov.market.auth.dto.AuthRequest
import dev.zornov.market.auth.dto.AuthResponse
import dev.zornov.market.auth.model.User
import dev.zornov.market.auth.repository.UserRepository
import dev.zornov.market.auth.security.JwtService
import dev.zornov.market.auth.service.TempKeyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val tempKeyService: TempKeyService,
    private val jwtService: JwtService
) {

    @PostMapping
    fun auth(@RequestBody req: AuthRequest): ResponseEntity<AuthResponse> {
        val user = userRepository.findById(req.id).orElse(null)
        if (user == null) {
            val key = tempKeyService.createOrGet(req.id, req.name)
            return ResponseEntity.ok(
                AuthResponse.UnauthorizedResponse(
                    tempKey = key.id,
                    validUntil = key.validUntilTimestamp()
                )
            )
        }

        return ResponseEntity.ok(AuthResponse.AuthorizedResponse(jwtService.generateToken(user)))
    }

    @PostMapping("/verify")
    fun verify(@RequestParam tempKey: String): ResponseEntity<Any> {
        val key = tempKeyService.findValid(tempKey) ?: return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(AuthResponse.WaitApproveResponse("Temp key is invalid or expired"))

        val user = userRepository.findById(key.userId).orElseGet {
            val newUser = User(key.userId, key.name)
            userRepository.save(newUser)
        }

        tempKeyService.consume(key)

        return ResponseEntity.ok(
            mapOf(
                "id" to user.id,
                "name" to user.name
            )
        )
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal jwt: Jwt): Map<String, Any?> =
        mapOf(
            "id" to jwt.subject,
            "name" to jwt.getClaim<String>("name"),
            "roles" to jwt.getClaim<List<String>>("roles")
        )
}
