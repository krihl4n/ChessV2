package com.krihl4n.persistence

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.GameOfChessCommand
import com.krihl4n.api.dto.MoveDto

class PersistableGameOfChess(private val delegate: GameOfChess, private val repo: MongoGamesRepository): GameOfChessCommand {

    override fun initialize() {
        println("!!! persist")
        delegate.initialize()
    }

    override fun playerReady(playerId: String, colorPreference: String?) {
        delegate.playerReady(playerId, colorPreference)
    }

    override fun resign(playerId: String) {
        delegate.resign(playerId)
    }

    override fun move(move: MoveDto) {
        delegate.move(move)
    }

    override fun undoMove() {
        delegate.undoMove()
    }

    override fun redoMove() {
        delegate.redoMove()
    }
}


