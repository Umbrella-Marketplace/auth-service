package dev.kachvev.serverscript.dto

data class SendMessageRequest(
    val chatId: Long,
    val message: String
)