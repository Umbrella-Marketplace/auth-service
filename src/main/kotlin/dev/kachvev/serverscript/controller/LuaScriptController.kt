package dev.kachvev.serverscript.controller

import dev.kachvev.serverscript.dto.LuaScriptResponse
import dev.kachvev.serverscript.repository.LuaScriptRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LuaScriptController(
    val luaScriptRepository: LuaScriptRepository
) {

    @GetMapping("/api/lua")
    fun getLuaScriptByName(
        @RequestParam name: String
    ): ResponseEntity<LuaScriptResponse> {
        val script = luaScriptRepository.findByName(name)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            LuaScriptResponse(
                name = script.name,
                content = script.content
            )
        )
    }
}
