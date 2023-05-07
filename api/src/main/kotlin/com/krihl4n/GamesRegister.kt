package com.krihl4n

import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRegister {

    private val games: MutableList<GameOfChess> = mutableListOf() // todo when to remove games?
    private val gamesSessionIds: MutableMap<String, MutableList<String>> = mutableMapOf() // unify those collections?
    private val playerSessionIds: MutableMap<String, String> = mutableMapOf()

    fun registerNewGame(gameOfChess: GameOfChess, sessionId: String) {
        games.add(gameOfChess)
        gamesSessionIds[gameOfChess.gameId] = mutableListOf(sessionId)
    }

    fun getRelatedSessionIds(gameId: String): List<String> {
        return gamesSessionIds[gameId].orEmpty()
    }

    fun getRelatedPlayerSessionIds(playerId: String): List<String> { // todo just return string
        val id = playerSessionIds[playerId]
        return if (id == null) emptyList() else listOf(id)
    }

    fun getGame(sessionId: String): GameOfChess? {
        return gamesSessionIds
            .filter { gamesSessionIds -> gamesSessionIds.value.contains(sessionId) }
            .map { it.key }
            .mapNotNull { this.games.find { game -> game.gameId == it } }
            .firstOrNull()
    }

    fun deregisterGame(gameId: String) {
        this.games.removeIf { it.gameId == gameId }
        this.gamesSessionIds.remove(gameId)
    }

    fun deregisterSession(sessionId: String) {
        gamesSessionIds.keys.forEach { gameId ->
            gamesSessionIds[gameId]?.remove(sessionId)
        }
        with(playerSessionIds.iterator()) {
            forEach { if (it.value == sessionId) remove() }
        }
//        playerSessionIds = playerSessionIds.filter { it.value != sessionId }
//        playerSessionIds.keys.forEach { playerId ->
//            playerSessionIds[playerId]?.remove(sessionId)
//        }
    }

    fun getGameById(gameId: String): GameOfChess {
        return this.games.first { it.gameId == gameId }
    }

    fun joinGame(sessionId: String, gameId: String) {
        val sessionIds = gamesSessionIds[gameId]
        if (sessionIds?.find { it == sessionId } != null) {
            return
        }
        sessionIds?.add(sessionId)
        sessionIds?.let { gamesSessionIds[gameId] = sessionIds }
    }

    fun joinPlayer(sessionId: String, playerId: String) {
        playerSessionIds[playerId] = sessionId
//        var sessionIds =  playerSessionIds[playerId]
//        if(sessionIds == null) {
//            sessionIds = mutableListOf()
//        }
//        sessionIds.add(sessionId)
//        playerSessionIds[playerId] = sessionIds
    }

    fun getPlayerId(sessionId: String): String? {
        return this.playerSessionIds.keys.firstOrNull { sessionId == this.playerSessionIds[it] }
        //return this.playerSessionIds.filter { it.value == sessionId }.first()
        //return this.playerSessionIds.firstNotNullOf { it.value.contains(sessionId) }
    }
}
