package com.krihl4n

import org.springframework.stereotype.Service
import java.util.*

@Service
class JoinGameHandler {

    private var joinCodes = mutableMapOf<String, String>()

    fun generateJoinCode(gameId: String): String {
        val joinCode = UUID.randomUUID().toString()
        joinCodes[joinCode] = gameId
        return joinCode
    }

    fun requestToJoinGame(joinCode: String): String {
        return joinCodes[joinCode] ?: throw RuntimeException("Incorrect join code")
    }

    fun notifyGameStarted(gameId: String) {
        joinCodes.filter { gameId == it.value }.keys.firstOrNull()?.let { this.joinCodes.remove(it) }
    }
}