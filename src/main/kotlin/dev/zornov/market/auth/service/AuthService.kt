package dev.zornov.market.auth.service

import dev.zornov.market.auth.model.Role
import dev.zornov.market.auth.model.User
import dev.zornov.market.auth.repository.UserRepository
import dev.zornov.market.auth.security.JwtService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    val userRepository: UserRepository,
    val tempKeyService: TempKeyService,
    val jwtService: JwtService,
    val kafka: KafkaTemplate<String, Any>
) {
    sealed class ApprovalMessage {
        data class Request(
            val token: String,
            val correlationId: String = UUID.randomUUID().toString()
        ) : ApprovalMessage()

        data class Response(
            val result: Result,
            val correlationId: String
        ) : ApprovalMessage() {
            sealed class Result {
                data class Success(
                    val userId: UUID,
                    val name: String
                ) : Result()

                data class Error(val message: String) : Result()
            }
        }
    }

    @KafkaListener(
        topics = ["approve-requests"],
        groupId = "auth-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleApproval(request: ApprovalMessage.Request) {
        val result = try {
            val user = approveUser(UUID.fromString(request.token))
            ApprovalMessage.Response.Result.Success(UUID.fromString(user.id), user.name)
        } catch (e: Exception) {
            ApprovalMessage.Response.Result.Error(e.message ?: "Unknown error")
        }

        val response = ApprovalMessage.Response(
            result = result,
            correlationId = request.correlationId
        )

        kafka.send("approve-responses", response)
    }

    fun approveUser(token: UUID): User {
        val key = tempKeyService.getKey(token) ?: throw IllegalArgumentException("Invalid token")
        return userRepository.findById(key.userId).orElseGet {
            userRepository.save(User(key.userId, key.name, listOf(Role.USER)))
        }
    }

    fun issueJwt(user: User): String = jwtService.generateToken(user)
}