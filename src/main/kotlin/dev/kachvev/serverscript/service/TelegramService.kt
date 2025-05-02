package dev.kachvev.serverscript.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TelegramService {

    val botToken = "7738587832:AAHe-DK6ptfFxI5gn46f-5s7SOCA57mIuVo"

    val restTemplate = RestTemplate()

    fun sendMessage(chatId: String, message: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val body = mapOf(
            "chat_id" to chatId,
            "text" to message
        )

        val request = HttpEntity(body, headers)

        try {
            restTemplate.postForEntity(url, request, String::class.java)
        } catch (e: Exception) {
            println("Ошибка при отправке сообщения в Telegram: ${e.message}")
        }
    }
}
