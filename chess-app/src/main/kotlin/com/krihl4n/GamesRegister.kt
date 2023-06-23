package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.persistence.GamesRepository
import com.krihl4n.persistence.PersistableGameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRegister(val repo: GamesRepository) {

    private val entries = mutableListOf<RegisteredSession>()

    fun observeGames(eventListener: GameEventListener) {
        this.repo.observeNewGames(eventListener)
    }

    fun debugPrint() {
        entries.forEach { println("\n$it") }
    }

    fun registerNewGame(gameOfChess: GameOfChess, sessionId: String) {
        entries.add(RegisteredSession(gameOfChess.gameId, sessionId))
        repo.saveNewGame(gameOfChess)
    }

    fun getRelatedSessionIds(gameId: String): Set<String> {
       return this.entries.find { it.gameId() == gameId }?.sessionIds().orEmpty()
    }

    fun getRelatedPlayerSessionId(playerId: String): String? { // todo just return string
        return  this.entries.find { it.playerParticipates(playerId) }?.playerSessionId(playerId)
    }

    fun getGame(sessionId: String): PersistableGameOfChess? { // todo not nullable?
        return entries.find { it.sessionParticipates(sessionId) }?.let { repo.getById(it.gameId) }
    }

    fun getGameForCommand(gameId: String): PersistableGameOfChess? { // todo not nullable?
        return repo.getById(gameId)
    }

    fun getGameForQuery(sessionId: String): GameOfChess? {
        return entries.find { it.sessionParticipates(sessionId) }?.let { repo.getForQuery(it.gameId) }
    }

    fun getGameForQueryById(gameId: String): GameOfChess {
        return repo.getForQuery(gameId)
    }

    fun deregisterGame(gameId: String) {
        this.entries.removeIf { it.gameId() == gameId }
    }

    fun deregisterSession(sessionId: String) {
        this.entries.forEach { it.deregisterSession(sessionId) }
     //   this.entries.removeIf { it.sessionIds().isEmpty() } // todo rethink that
    }

//    fun getGameById(gameId: String): GameOfChess {
//        return this.entries.first{it.gameId() == gameId}.game
//    }

    fun registerPlayer(sessionId: String, gameId: String, playerId: String) {
        if(this.entries.find { it.gameId() == gameId } == null) {
            entries.add(RegisteredSession(gameId, sessionId))
        }
        this.entries.find { it.gameId() == gameId }?.registerPlayer(playerId, sessionId)
    }

    fun getPlayerId(sessionId: String): String? {
        return this.entries.find { it.sessionParticipates(sessionId) }?.playerId(sessionId)
    }
}

class RegisteredSession(val gameId: String, private var initialSessionId: String?) {
    private val playerSessions = mutableListOf<PlayerSession>()

    fun gameId() = this.gameId

    fun sessionIds() : Set<String> {
        val sessions = playerSessions.map { it.sessionId }.toMutableSet()
        this.initialSessionId?.let { sessions.add(it) }
        return sessions.toSet()
    }

    fun sessionParticipates(sessionId: String) = this.sessionIds().contains(sessionId)

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