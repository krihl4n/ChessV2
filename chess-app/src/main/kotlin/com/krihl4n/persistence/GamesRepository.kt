package com.krihl4n.persistence

import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service

@Service
class GamesRepository(private val mongoGamesRepository: MongoGamesRepository) {

    private val tmpList = mutableListOf<GameOfChess>()

    fun saveNewGame(gameOfChess: GameOfChess) {
        tmpList.add(gameOfChess)
        mongoGamesRepository.save(GameDocument(gameOfChess.gameId, gameOfChess.gameMode))
    }

    fun getById(gameId: String): PersistableGameOfChess? {
        return tmpList.firstOrNull{it.gameId == gameId}?.let { PersistableGameOfChess(it, mongoGamesRepository) }
    }

    fun getForQuery(gameId: String): GameOfChess { // todo interface for query?
        return tmpList.first{it.gameId == gameId}
    }
}