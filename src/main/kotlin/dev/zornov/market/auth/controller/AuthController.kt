package dev.zornov.market.auth.controller

import dev.zornov.market.auth.dto.AuthRequest
import dev.zornov.market.auth.dto.AuthResponse
import dev.zornov.market.auth.repository.UserRepository
import dev.zornov.market.auth.service.AuthService
import dev.zornov.market.auth.service.LoginApprovalService
import dev.zornov.market.auth.service.TempKeyService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val tempKeyService: TempKeyService,
    private val authService: AuthService,
    private val loginApprovalService: LoginApprovalService
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

        return when (loginApprovalService.startRequest(req.id)) {
            LoginApprovalService.RequestStatus.APPROVED -> {
                ResponseEntity.ok(AuthResponse.AuthorizedResponse(authService.issueJwt(user)))
            }
            LoginApprovalService.RequestStatus.NEW -> {
                ResponseEntity.accepted()
                    .body(AuthResponse.WaitApproveResponse("Please approve login request"))
            }
            LoginApprovalService.RequestStatus.ALREADY_PENDING -> {
                ResponseEntity.accepted()
                    .body(AuthResponse.WaitApproveResponse("Login request still pending"))
            }
        }
    }
}
