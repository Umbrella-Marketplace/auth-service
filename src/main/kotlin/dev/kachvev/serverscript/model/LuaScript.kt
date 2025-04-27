package dev.kachvev.serverscript.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "scripts")
data class LuaScript(
    @Id
    val id: String? = null,

    val name: String,
    val author: String = "None",
    val price: Double = 0.0,
    val content: String,

    val createdAt: Instant = Instant.now()
)