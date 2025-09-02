package dev.zornov.market.auth.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.zornov.market.auth.dto.ApprovalInfo
import dev.zornov.market.auth.dto.ApprovalMessage
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class LoginApprovalService(
    private val kafka: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    private val loginRequests = ConcurrentHashMap<String, ApprovalInfo>()
    enum class RequestStatus { NEW, ALREADY_PENDING, APPROVED }

    @KafkaListener(topics = ["approval-responses"], groupId = "auth-service")
    fun handleApproval(rawMessage: String) {
        val response = runCatching {
            objectMapper.readValue<ApprovalMessage.Response>(rawMessage)
        }.getOrNull() ?: return

        val now = Instant.now().toEpochMilli()

        when (val result = response.result) {
            is ApprovalMessage.Response.Result.Success -> {
                loginRequests[result.userId] = ApprovalInfo(true, now)
            }
            is ApprovalMessage.Response.Result.Error -> {
                loginRequests[result.userId] = ApprovalInfo(false, now)
            }
        }
    }

    fun startRequest(userId: String): RequestStatus {
        val now = Instant.now().toEpochMilli()

        val existing = loginRequests[userId]
        if (existing != null) {
            val expired = now - existing.timestamp > 30 * 60 * 1000L
            if (!expired) {
                return if (existing.approved) RequestStatus.APPROVED else RequestStatus.ALREADY_PENDING
            } else {
                loginRequests.remove(userId)
            }
        }

        loginRequests[userId] = ApprovalInfo(false, now)
        val json = objectMapper.writeValueAsString(ApprovalMessage.Request(userId))
        kafka.send("approval-requests", userId, json)
        return RequestStatus.NEW
    }

    fun isApproved(userId: String): Boolean {
        val info = loginRequests[userId] ?: return false
        val now = Instant.now().toEpochMilli()
        val expired = now - info.timestamp > 30 * 60 * 1000L
        if (expired) {
            loginRequests.remove(userId)
            return false
        }
        return info.approved
    }
}
