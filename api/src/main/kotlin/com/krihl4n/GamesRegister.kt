package com.krihl4n

import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRegister {

    private val games: MutableList<GameOfChess> = mutableListOf() // todo when to remove games?
    private val gamesSessionIds: MutableMap<String, MutableList<String>> = mutableMapOf()
    private val playerSessionIds: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun reqisterNewGame(gameOfChess: GameOfChess, sessionId: String) {
        games.add(gameOfChess)
        gamesSessionIds[gameOfChess.gameId] = mutableListOf(sessionId)
    }

    fun getRelatedSessionIds(gameId: String): List<String> {
        return gamesSessionIds[gameId].orEmpty()
    }

    fun getRelatedPlayerSessionIds(playerId: String): List<String> {
        return playerSessionIds[playerId].orEmpty()
    }

    fun getGame(sessionId: String): GameOfChess? {
        val gameId: String = gamesSessionIds
            .filter { gamesSessionIds -> gamesSessionIds.value.contains(sessionId) }
            .map { it.key }
            .first()
        return this.games.find { it.gameId == gameId }
    }

    fun deregisterSession(sessionId: String) {
        gamesSessionIds.keys.forEach { gameId ->
            gamesSessionIds[gameId]?.remove(sessionId)
        }
        playerSessionIds.keys.forEach { playerId ->
            playerSessionIds[playerId]?.remove(sessionId)
        }
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
        var sessionIds =  playerSessionIds[playerId]
        if(sessionIds == null) {
            sessionIds = mutableListOf()
        }
        sessionIds.add(sessionId)
        playerSessionIds[playerId] = sessionIds
    }
}
