package com.krihl4n.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.GameOfChessCommand
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.persistence.GamesRepository.CommandType
import com.krihl4n.persistence.GamesRepository.CommandType.*
import java.time.Instant
import kotlin.jvm.optionals.getOrNull

class PersistableGameOfChess(private val delegate: GameOfChess, private val repo: MongoGamesRepository) :
    GameOfChessCommand {

    private val objectMapper = ObjectMapper()

    override fun initialize() {
        save(INITIALIZE)
        delegate.initialize() // todo check if thrown exception rollbacks saved doc
    }

    override fun playerReady(playerId: String, colorPreference: String?) {
        save(PLAYER_READY, PlayerReadyData(playerId, colorPreference))
        delegate.playerReady(playerId, colorPreference)
    }

    override fun resign(playerId: String) {
        save(RESIGN, ResignData(playerId))
        delegate.resign(playerId)
    }

    override fun move(move: MoveDto) {
        save(MOVE, MoveData(move.playerId, move.from, move.to, move.pawnPromotion))
        delegate.move(move)
    }

    override fun undoMove() {
        save(UNDO_MOVE)
        delegate.undoMove()
    }

    override fun redoMove() {
        save(REDO_MOVE)
        delegate.redoMove()
    }

    private fun save(type: CommandType, data: Any? = null) {
        repo.findById(delegate.gameId).getOrNull()
            ?.let {
                it.commands.add(
                    Command(
                        type,
                        Instant.now(),
                        data?.let { objectMapper.writeValueAsString(data) })
                )
                repo.save(it)
            }
    }
}