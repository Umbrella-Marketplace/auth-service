package dev.kachvev.serverscript.ext

fun String.splitCallback(): List<String> {
    return this.split(":")
}

fun String.parseCallback(): Pair<String, String?> {
    val parts = this.split(":")
    val command = parts.getOrNull(0) ?: ""
    val param = parts.getOrNull(1)
    return command to param
}

fun String.isCallback(command: String): Boolean {
    return this.startsWith(command)
}
