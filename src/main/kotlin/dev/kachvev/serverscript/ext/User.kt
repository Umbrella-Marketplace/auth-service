package dev.kachvev.serverscript.ext

import dev.kachvev.serverscript.model.User
import java.time.Duration
import java.time.ZoneId

fun User.toProfileText(): String {
    val now = nowMoscow()

    return buildString {
        appendLine("👤 *Профиль*")
        appendLine()
        appendLine("• Никнейм: `${nickname}`")
        appendLine("• Подписка активна: `${if (isSubscriptionActive()) "✅ Да" else "❌ Нет"}`")

        if (isSubscriptionActive()) {
            val subscriptionTime = subscriptionUntil
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(moscowZone)
                .toLocalDateTime()
            val remaining = Duration.between(now, subscriptionTime)

            append("• Подписка до: `${subscriptionTime.formatAsDateTime()}`")
            if (!remaining.isNegative) {
                append(" (${remaining.toReadableDuration()})")
            }
            appendLine()
        }

        appendLine("• Последний вход: `${lastLogin.formatTimeAgo()}`")
    }
}