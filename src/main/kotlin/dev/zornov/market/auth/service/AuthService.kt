package dev.zornov.market.auth.service

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
    private val userRepository: UserRepository,
    private val tempKeyService: TempKeyService,
    private val jwtService: JwtService,
    private val kafka: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    sealed interface VerificationMessage {
        data class Request(val token: String, val correlationId: String) : VerificationMessage

        data class Response(
            val result: Result,
            val correlationId: String
        ) : VerificationMessage {
            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
            @JsonSubTypes(
                JsonSubTypes.Type(value = Result.Success::class, name = "Success"),
                JsonSubTypes.Type(value = Result.Error::class, name = "Error")
            )
            sealed interface Result {
                @JsonTypeName("Success")
                data class Success(val userId: String, val name: String) : Result

                @JsonTypeName("Error")
                data class Error(val message: String) : Result
            }
        }
    }

    @KafkaListener(topics = ["verification-requests"], groupId = "auth-service")
    fun handleApproval(rawMessage: String) {
        val request = runCatching { objectMapper.readValue<VerificationMessage.Request>(rawMessage) }.getOrNull() ?: return

        val token = runCatching { UUID.fromString(request.token) }.getOrNull()
            ?: return sendError("Invalid token format", request.correlationId)

        val key = tempKeyService.getKey(token)
            ?: return sendError("Invalid token", request.correlationId)

        val user = userRepository.findById(key.userId)
            .orElseGet { userRepository.save(User(key.userId, key.name, listOf(Role.USER))) }

        sendResponse(
            VerificationMessage.Response.Result.Success(user.id, user.name),
            request.correlationId
        )
    }

    private fun sendError(message: String, correlationId: String) =
        sendResponse(VerificationMessage.Response.Result.Error(message), correlationId)

    private fun sendResponse(result: VerificationMessage.Response.Result, correlationId: String) {
        val response = VerificationMessage.Response(result, correlationId)
        kafka.send("verification-responses", objectMapper.writeValueAsString(response))
    }

    fun issueJwt(user: User): String = jwtService.generateToken(user)
}
