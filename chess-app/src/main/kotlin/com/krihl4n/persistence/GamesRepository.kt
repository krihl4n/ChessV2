package com.krihl4n.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.krihl4n.gamesManagement.GameOfChessCreator
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.persistence.GamesRepository.CommandType.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class GamesRepository(private val mongoGamesRepository: MongoGamesRepository, private val creator: GameOfChessCreator) {

    private val games = mutableListOf<GameOfChess>()
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun saveNewGame(gameOfChess: GameOfChess) {
        games.add(gameOfChess)
        mongoGamesRepository.save(GameDocument(gameOfChess.gameId, gameOfChess.gameMode))
    }

    fun getGameForCommand(gameId: String): PersistableGameOfChess {
        val gameOfChess =
            games.find { it.gameId == gameId }?:
            retrieveGameOfChess(gameId).also {
                games.add(it)
            }
        return PersistableGameOfChess(gameOfChess, mongoGamesRepository)
    }

    fun getGameForQuery(gameId: String): ReadOnlyGameOfChess {
        val gameOfChess =  games.find { it.gameId == gameId } ?:
        retrieveGameOfChess(gameId).also { games.add(it) }
        return ReadOnlyGameOfChess(gameOfChess)
    }

    private fun retrieveGameOfChess(gameId: String): GameOfChess {
        val persistedGame: GameDocument = mongoGamesRepository.findById(gameId).getOrNull() ?: throw RuntimeException("Game not found")
        val gameOfChess = creator.createWithoutListeners(persistedGame.id, persistedGame.gameMode)
        for (command in persistedGame.commands) {
            logger.debug("process --> {}", command)
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

                UNDO_MOVE -> {
                    val data = objectMapper.readValue(command.data, UndoMoveData::class.java)
                    gameOfChess.undoMove(data.playerId)
                }
                RESIGN -> {
                    val data = objectMapper.readValue(command.data, ResignData::class.java)
                    gameOfChess.resign(data.playerId)
                }
            }
        }
        creator.registerListeners(gameOfChess)
        return gameOfChess
    }

    enum class CommandType {
        INITIALIZE, PLAYER_READY, MOVE, UNDO_MOVE, RESIGN
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GamesRepository::class.java)
    }
}