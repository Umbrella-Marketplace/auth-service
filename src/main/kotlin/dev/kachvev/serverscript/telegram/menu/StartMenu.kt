package dev.kachvev.serverscript.telegram.menu

import com.github.kotlintelegrambot.dispatcher.handlers.CommandHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import dev.kachvev.serverscript.telegram.enum.Buttons

fun CommandHandlerEnvironment.startMenu(isLinked: Boolean) {
    val buttons = if (isLinked) {
        listOf(
            listOf(
                InlineKeyboardButton.CallbackData("👤 Профиль", Buttons.StartMenu.PROFILE),
                InlineKeyboardButton.CallbackData("🛒 Маркетплейс", Buttons.StartMenu.MARKETPLACE),
            )
        )
    } else {
        listOf(
            listOf(
                InlineKeyboardButton.CallbackData("🔗 Привязать аккаунт", Buttons.StartMenu.LINK_ACCOUNT)
            )
        )
    }

    bot.sendMessage(
        chatId = ChatId.fromId(message.chat.id),
        text = "👋 Добро пожаловать в сервер-скрипт бот!",
        replyMarkup = InlineKeyboardMarkup.create(buttons)
    )
}