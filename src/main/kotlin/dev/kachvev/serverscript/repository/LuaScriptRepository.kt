package dev.kachvev.serverscript.repository

import dev.kachvev.serverscript.model.LuaScript
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LuaScriptRepository : MongoRepository<LuaScript, String> {
    fun findByName(name: String): LuaScript?
    fun findByAuthor(author: String): List<LuaScript>
}
