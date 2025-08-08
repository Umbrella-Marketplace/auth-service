package dev.zornov.market.auth.service

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class LoginApprovalService(
    private val kafka: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val loginRequests = ConcurrentHashMap<String, Boolean>()

    enum class RequestStatus { NEW, ALREADY_PENDING, APPROVED }

    sealed interface ApprovalMessage {
        data class Request(val userId: String) : ApprovalMessage

        data class Response(val result: Result) : ApprovalMessage {
            @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
            @JsonSubTypes(
                JsonSubTypes.Type(value = Result.Success::class, name = "Success"),
                JsonSubTypes.Type(value = Result.Error::class, name = "Error")
            )
            sealed interface Result {
                @JsonTypeName("Success")
                data class Success(val userId: String) : Result

                @JsonTypeName("Error")
                data class Error(val userId: String) : Result
            }
        }
    }

    @KafkaListener(topics = ["approval-responses"], groupId = "auth-service")
    fun handleApproval(rawMessage: String) {
        val response = runCatching {
            objectMapper.readValue<ApprovalMessage.Response>(rawMessage)
        }.getOrNull() ?: return

        when (val result = response.result) {
            is ApprovalMessage.Response.Result.Success -> {
                loginRequests[result.userId] = true
            }
            is ApprovalMessage.Response.Result.Error -> {
                loginRequests[result.userId] = false
            }
        }
    }

    fun startRequest(userId: String): RequestStatus {
        return when {
            loginRequests[userId] == true -> RequestStatus.APPROVED
            loginRequests.containsKey(userId) -> RequestStatus.ALREADY_PENDING
            else -> {
                loginRequests[userId] = false
                val json = objectMapper.writeValueAsString(ApprovalMessage.Request(userId))
                kafka.send("approval-requests", userId, json)
                RequestStatus.NEW
            }
        }
    }

    fun isApproved(userId: String): Boolean = loginRequests[userId] ?: false
}
