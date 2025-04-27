package dev.kachvev.serverscript.controller

import dev.kachvev.serverscript.dto.SendMessageRequest
import dev.kachvev.serverscript.service.TelegramService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telegram")
class TelegramController(
    @Autowired val telegramService: TelegramService
) {

    @PostMapping("/send")
    fun sendMessage(@RequestBody request: SendMessageRequest) {
        println("Sending message to chatId: ${request.chatId}, message: ${request.message}")
        telegramService.sendMessage(request.chatId, request.message)
    }
}