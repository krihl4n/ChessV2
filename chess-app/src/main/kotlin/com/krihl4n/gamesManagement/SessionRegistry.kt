package com.krihl4n.gamesManagement

import org.springframework.stereotype.Service

@Service
class SessionRegistry {

    private val entries = mutableListOf<RegisteredGameSessions>()

    fun registerNewGame(gameId: String, sessionId: String) {
        synchronized(this) {
            entries.add(RegisteredGameSessions(gameId, sessionId))
        }
    }

    fun getRelatedSessionIds(gameId: String): Set<String> {
        return this.entries.find { it.gameId() == gameId }?.sessionIds().orEmpty()
    }

    fun getRelatedPlayerSessionId(gameId: String, playerId: String): String? { // todo just return string
        return this.entries.filter { it.gameId == gameId }.find { it.playerParticipates(playerId) }
            ?.playerSessionId(playerId)
    }

    fun deregisterGame(gameId: String) {
        synchronized(this) {
            this.entries.removeIf { it.gameId() == gameId }
        }
    }

    fun deregisterSession(sessionId: String) {
        synchronized(this) {
            this.entries.forEach { it.deregisterSession(sessionId) }
            this.entries.removeIf { it.sessionIds().isEmpty() } // todo rethink that
        }
    }

    fun registerPlayer(sessionId: String, gameId: String, playerId: String) {
        synchronized(this) {
            if (this.entries.find { it.gameId() == gameId } == null) {
                entries.add(RegisteredGameSessions(gameId, sessionId))
            }
            this.entries.find { it.gameId() == gameId }?.registerPlayer(playerId, sessionId)
        }
    }
}

class RegisteredGameSessions(val gameId: String, private var initialSessionId: String?) {
    private val playerSessions = mutableListOf<PlayerSession>()

    fun gameId() = this.gameId

    fun sessionIds(): Set<String> {
        val sessions = playerSessions.map { it.sessionId }.toMutableSet()
        this.initialSessionId?.let { sessions.add(it) }
        return sessions.toSet()
    }

    fun playerParticipates(playerId: String) = this.playerSessions.map { it.playerId }.contains(playerId)

    fun playerSessionId(playerId: String) = this.playerSessions.first { it.playerId == playerId }.sessionId

    fun playerId(sessionId: String) = this.playerSessions.find { it.sessionId == sessionId }?.playerId

    fun deregisterSession(sessionId: String) {
        if (this.initialSessionId == sessionId) {
            this.initialSessionId = null
        }
        this.playerSessions.removeIf { it.sessionId == sessionId }
    }

    fun registerPlayer(playerId: String, sessionId: String) {
        this.playerSessions.add(PlayerSession(playerId, sessionId))
    }

    override fun toString(): String {
        return "RegisteredGame(\ngame=${gameId}, \ninitialSessionId=$initialSessionId, \nplayerSessions=\n${this.playerSessions.map { "$it" }})"
    }
}

data class PlayerSession(val playerId: String, val sessionId: String)