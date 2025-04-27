package dev.kachvev.serverscript.service

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service

@Service
class MongoHealthChecker(
    val mongoTemplate: MongoTemplate
) {
    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @PostConstruct
    fun startHealthChecks() {
        scope.launch {
            while (isActive) {
                try {
                    mongoTemplate.db.runCommand(org.bson.Document("ping", 1))
                } catch (e: Exception) {
                    println("MongoDB connection is dead ❌: ${e.message}")
                }
                delay(30_000)
            }
        }
    }
}