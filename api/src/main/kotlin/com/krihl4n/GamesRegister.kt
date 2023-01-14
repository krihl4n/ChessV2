package com.krihl4n

import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRegister {

    private val games: MutableList<GameOfChess> = mutableListOf() // todo when to remove games?
    private val gamesSessionIds: MutableMap<String, MutableList<PlayerSessions>> = mutableMapOf()

    fun reqisterNewGame(gameOfChess: GameOfChess, sessionId: String) {
        games.add(gameOfChess)
        gamesSessionIds[gameOfChess.gameId] = mutableListOf(PlayerSessions(true, sessionId))
    }

    fun getRelatedSessionIds(gameId: String): List<String> {
        return gamesSessionIds[gameId]?.map { it.sessionId } ?: emptyList()
    }

    fun getRelatedPlayerSessionIds(gameId: String): List<PlayerSessions> {
        return gamesSessionIds[gameId] ?: emptyList()
    }

    // TODO if many games per session, each request must have game id
    // for now one game per session
    // can there be many?
    fun getGame(sessionId: String): GameOfChess? {
        val gameId: String = gamesSessionIds
            .filter { playerSessions -> playerSessions.value.map { it.sessionId }.contains(sessionId) }
            .map { it.key }
            .first()
        return this.games.find { it.gameId == gameId }
    }

    fun deregisterSession(sessionId: String) {
        gamesSessionIds.keys.forEach { gameId ->
            gamesSessionIds[gameId]
                ?.filter { it.sessionId != sessionId }
                ?.let { gamesSessionIds[gameId] = it.toMutableList() }
        }
    }

    fun getGameById(gameId: String): GameOfChess {
        return this.games.first { it.gameId == gameId }
    }

    fun registerPlayer(sessionId: String, gameId: String, playerId: String) {
        //gamesSessionIds[gameId]?.remove(PlayerSessions(null, sessionId))
    }

    fun joinGame(sessionId: String, gameId: String) {
        val sessionIds = gamesSessionIds[gameId]
        if (sessionIds?.find { it.sessionId == sessionId } != null) {
            return
        }
        sessionIds?.add(PlayerSessions(false, sessionId)) // TODO something better with player management here
        sessionIds?.let { gamesSessionIds[gameId] = sessionIds }
    }

    data class PlayerSessions(val isPlayerOne: Boolean, val sessionId: String)
}
