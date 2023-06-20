package com.krihl4n.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.persistence.GamesRepository.CommandType.*
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class GamesRepository(private val mongoGamesRepository: MongoGamesRepository) {

    private val games = mutableListOf<GameOfChess>()
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun saveNewGame(gameOfChess: GameOfChess) {
        games.add(gameOfChess)
        mongoGamesRepository.save(GameDocument(gameOfChess.gameId, gameOfChess.gameMode))
    }

    fun getById(gameId: String): PersistableGameOfChess? {
        val gameOfChess =
            games.find { it.gameId == gameId }?:
            retrieveGameOfChess(gameId).also { games.add(it) }
        return PersistableGameOfChess(gameOfChess, mongoGamesRepository)
       // return tmpList.firstOrNull{it.gameId == gameId}?.let { PersistableGameOfChess(it, mongoGamesRepository) }
    }

    fun getForQuery(gameId: String): GameOfChess { // todo interface for query?
        return games.find { it.gameId == gameId } ?:
        retrieveGameOfChess(gameId).also { games.add(it) }

    //return retrieveGameOfChess(gameId)
        //return tmpList.first{it.gameId == gameId}
    }

    private fun retrieveGameOfChess(gameId: String): GameOfChess {
        val persistedGame: GameDocument = mongoGamesRepository.findById(gameId).getOrNull() ?: throw RuntimeException("Game not found")
        val gameOfChess = GameOfChess(persistedGame.id, persistedGame.gameMode, null)

        for (command in persistedGame.commands) {
            when (command.type) {
                INITIALIZE -> gameOfChess.initialize()
                PLAYER_READY -> {
                    val data = objectMapper.readValue(command.data, PlayerReadyData::class.java)
                    gameOfChess.playerReady(data.playerId, data.colorPreference)
                }
                MOVE -> {
                    val data = objectMapper.readValue(command.data, MoveData::class.java)
                    gameOfChess.move(MoveDto(data.playerId, data.from, data.to, data.pawnPromotion))
                }

                UNDO_MOVE -> gameOfChess.undoMove()
                REDO_MOVE -> gameOfChess.redoMove()
                RESIGN -> {
                    val data = objectMapper.readValue(command.data, ResignData::class.java)
                    gameOfChess.resign(data.playerId)
                }
            }
        }
        return gameOfChess
    }

    enum class CommandType {
        INITIALIZE, PLAYER_READY, MOVE, UNDO_MOVE, REDO_MOVE, RESIGN
    }
}