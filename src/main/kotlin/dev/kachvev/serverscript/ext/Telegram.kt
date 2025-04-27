package dev.kachvev.serverscript.ext

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import java.net.URL

fun CommandHandlerEnvironment.reply(text: String) {
    bot.sendMessage(chatId = ChatId.Companion.fromId(message.chat.id), text = text)
}

fun TextHandlerEnvironment.reply(text: String) {
    bot.sendMessage(chatId = ChatId.Companion.fromId(message.chat.id), text = text)
}

fun downloadFileContent(fileId: String): String? {
    val filePath = getFilePathHardcore("7738587832:AAHe-DK6ptfFxI5gn46f-5s7SOCA57mIuVo", fileId) ?: return null
    return downloadFileContentHardcore("7738587832:AAHe-DK6ptfFxI5gn46f-5s7SOCA57mIuVo", filePath)
}

fun getFilePathHardcore(botToken: String, fileId: String): String? = runCatching {
    val url = URL("https://api.telegram.org/bot$botToken/getFile?file_id=$fileId")
    val responseText = url.readText()

    if (!responseText.contains("\"ok\":true")) {
        println("❌ Ошибка ответа Telegram API: $responseText")
        return@runCatching null
    }

    responseText.substringAfter("\"file_path\":\"").substringBefore("\"").takeIf { it.isNotBlank() }
}.getOrNull()

fun downloadFileContentHardcore(botToken: String, filePath: String): String? = runCatching {
    URL("https://api.telegram.org/file/bot$botToken/$filePath")
        .openStream()
        .bufferedReader()
        .readText()
}.getOrNull()