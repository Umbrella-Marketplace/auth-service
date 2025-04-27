package dev.kachvev.serverscript.wrapper

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.handlers.media.MediaHandlerEnvironment
import com.github.kotlintelegrambot.entities.files.*
import com.github.kotlintelegrambot.entities.stickers.Sticker

class TelegramAwaiter {

    val awaitingText = mutableMapOf<Long, suspend (TextHandlerEnvironment) -> Unit>()
    val awaitingDocument = mutableMapOf<Long, suspend (MediaHandlerEnvironment<Document>) -> Unit>()
    val awaitingPhoto = mutableMapOf<Long, suspend (MediaHandlerEnvironment<List<PhotoSize>>) -> Unit>()
    val awaitingVideo = mutableMapOf<Long, suspend (MediaHandlerEnvironment<Video>) -> Unit>()
    val awaitingAudio = mutableMapOf<Long, suspend (MediaHandlerEnvironment<Audio>) -> Unit>()
    val awaitingSticker = mutableMapOf<Long, suspend (MediaHandlerEnvironment<Sticker>) -> Unit>()
    val awaitingVoice = mutableMapOf<Long, suspend (MediaHandlerEnvironment<Voice>) -> Unit>()


    fun awaitText(chatId: Long, handler: suspend (TextHandlerEnvironment) -> Unit) {
        awaitingText[chatId] = handler
    }

    fun awaitDocument(chatId: Long, handler: suspend (MediaHandlerEnvironment<Document>) -> Unit) {
        awaitingDocument[chatId] = handler
    }

    fun awaitPhoto(chatId: Long, handler: suspend (MediaHandlerEnvironment<List<PhotoSize>>) -> Unit) {
        awaitingPhoto[chatId] = handler
    }

    fun awaitVideo(chatId: Long, handler: suspend (MediaHandlerEnvironment<Video>) -> Unit) {
        awaitingVideo[chatId] = handler
    }

    fun awaitAudio(chatId: Long, handler: suspend (MediaHandlerEnvironment<Audio>) -> Unit) {
        awaitingAudio[chatId] = handler
    }

    fun awaitSticker(chatId: Long, handler: suspend (MediaHandlerEnvironment<Sticker>) -> Unit) {
        awaitingSticker[chatId] = handler
    }

    fun awaitVoice(chatId: Long, handler: suspend (MediaHandlerEnvironment<Voice>) -> Unit) {
        awaitingVoice[chatId] = handler
    }

    // ==== Методы для обработки событий =====

    suspend fun handleIfAwaitingText(env: TextHandlerEnvironment): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingText.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingDocument(env: MediaHandlerEnvironment<Document>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingDocument.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingPhoto(env: MediaHandlerEnvironment<List<PhotoSize>>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingPhoto.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingVideo(env: MediaHandlerEnvironment<Video>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingVideo.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingAudio(env: MediaHandlerEnvironment<Audio>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingAudio.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingSticker(env: MediaHandlerEnvironment<Sticker>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingSticker.remove(chatId) ?: return false

        handler(env)
        return true
    }

    suspend fun handleIfAwaitingVoice(env: MediaHandlerEnvironment<Voice>): Boolean {
        val chatId = env.message.chat.id
        val handler = awaitingVoice.remove(chatId) ?: return false

        handler(env)
        return true
    }
}
