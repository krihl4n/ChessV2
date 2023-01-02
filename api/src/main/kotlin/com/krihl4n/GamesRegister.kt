package com.krihl4n

import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRegister {

    private val games: MutableList<GameOfChess> = mutableListOf() // todo when to remove games?
    private val gamesSessionIds: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun reqisterNewGame(gameOfChess: GameOfChess, sessionId: String) {
        games.add(gameOfChess)
        gamesSessionIds[gameOfChess.gameId] = mutableListOf(sessionId)
    }

    fun getRelatedSessionIds(gameId: String): List<String> {
        return gamesSessionIds[gameId] ?: emptyList()
    }

    // TODO if many games per session, each request must have game id
    // for now one game per session
    // can there be many?
    fun getGame(sessionId: String): GameOfChess? {
        val gameId: String = gamesSessionIds
            .filter { it.value.contains(sessionId) }
            .map { it.key }
            .first()
        return this.games.find { it.gameId == gameId }
    }

    fun deregisterSession(sessionId: String) {
        gamesSessionIds.keys.forEach { gameId ->
            gamesSessionIds[gameId]
                ?.filter { it != sessionId }
                ?.let { gamesSessionIds[gameId] = it.toMutableList() }
        }
    }

    fun getGameById(gameId: String): GameOfChess {
        return this.games.first { it.gameId == gameId }
    }

    fun registerSessionForGame(sessionId: String, gameId: String) {
        val sessionIds = gamesSessionIds[gameId]
        sessionIds?.add(sessionId)
        sessionIds?.let { gamesSessionIds[gameId] = sessionIds }
    }
}
