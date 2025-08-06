package dev.zornov.market.auth.service

import dev.zornov.market.auth.model.Role
import dev.zornov.market.auth.model.User
import dev.zornov.market.auth.repository.UserRepository
import dev.zornov.market.auth.security.JwtService
import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    val userRepository: UserRepository,
    val tempKeyService: TempKeyService,
    val jwtService: JwtService,
    val kafka: KafkaTemplate<String, String>
) {
    val objectMapper = jacksonObjectMapper()

    sealed interface ApprovalMessage {
        data class Request(
            val token: String,
            val correlationId: String
        ): ApprovalMessage

        data class Response(
            val result: Result,
            val correlationId: String
        ) : ApprovalMessage {
            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
            @JsonSubTypes(
                JsonSubTypes.Type(value = Result.Success::class, name = "Success"),
                JsonSubTypes.Type(value = Result.Error::class, name = "Error")
            )
            sealed class Result {
                @JsonTypeName("Success")
                data class Success(val userId: String, val name: String) : Result()
                @JsonTypeName("Error")
                data class Error(val message: String) : Result()
            }
        }
    }

    @KafkaListener(topics = ["approve-requests"], groupId = "auth-service")
    fun handleApproval(rawMessage: String) {
        val request = runCatching { objectMapper.readValue<ApprovalMessage.Request>(rawMessage) }.getOrNull() ?: return

        val token = runCatching { UUID.fromString(request.token) }.getOrNull()
            ?: run {
                sendErrorResponse("Invalid token format", request.correlationId)
                return
            }

        val key = tempKeyService.getKey(token)
            ?: run {
                sendErrorResponse("Invalid token", request.correlationId)
                return
            }

        val user = userRepository.findById(key.userId)
            .orElseGet { userRepository.save(User(key.userId, key.name, listOf(Role.USER))) }

        val successResult = ApprovalMessage.Response.Result.Success(user.id, user.name)
        sendResponse(successResult, request.correlationId)
    }

    private fun sendErrorResponse(message: String, correlationId: String) {
        val errorResult = ApprovalMessage.Response.Result.Error(message)
        sendResponse(errorResult, correlationId)
    }

    private fun sendResponse(result: ApprovalMessage.Response.Result, correlationId: String) {
        val response = ApprovalMessage.Response(result, correlationId)
        kafka.send("approve-responses", objectMapper.writeValueAsString(response))
    }

    fun issueJwt(user: User): String = jwtService.generateToken(user)
}