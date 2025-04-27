package dev.kachvev.serverscript.ext

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val moscowZone: ZoneId = ZoneId.of("Europe/Moscow")
val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

fun nowMoscow(): LocalDateTime = LocalDateTime.now(moscowZone)

fun LocalDateTime.formatAsDateTime(): String {
    return format(dateTimeFormatter)
}

fun LocalDateTime.formatTimeAgo(): String {
    val now = nowMoscow()
    val duration = Duration.between(this, now)

    return when {
        duration.toMinutes() < 1 -> "только что"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} ${duration.toMinutes().pluralize("минута", "минуты", "минут")} назад"
        duration.toHours() < 24 -> "${duration.toHours()} ${duration.toHours().pluralize("час", "часа", "часов")} назад"
        duration.toDays() < 7 -> "${duration.toDays()} ${duration.toDays().pluralize("день", "дня", "дней")} назад"
        else -> "${duration.toDays().div(7L)} ${duration.toDays().div(7L).pluralize("неделя", "недели", "недель")} назад"
    }
}

fun Duration.toReadableDuration(): String {
    return when {
        toMinutes() < 1 -> "меньше минуты"
        toMinutes() < 60 -> "${toMinutes()} ${toMinutes().pluralize("минута", "минуты", "минут")}"
        toHours() < 24 -> "${toHours()} ${toHours().pluralize("час", "часа", "часов")}"
        toDays() < 7 -> "${toDays()} ${toDays().pluralize("день", "дня", "дней")}"
        else -> "${toDays().div(7L)} ${toDays().div(7L).pluralize("неделя", "недели", "недель")}"
    }
}

fun Long.pluralize(one: String, few: String, many: String): String {
    val value = this % 100
    return if (value in 11..19) {
        many
    } else when (value % 10) {
        1L -> one
        2L, 3L, 4L -> few
        else -> many
    }
}