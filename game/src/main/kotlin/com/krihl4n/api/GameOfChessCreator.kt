package com.krihl4n.api

import com.krihl4n.api.dto.PlayerDto
import com.krihl4n.api.pieceSetups.SetupProvider
import com.krihl4n.model.Color
import java.util.*

object GameOfChessCreator {

    fun createGame(setup: String?, listener: GameEventListener): GameOfChess {
        return setupNewGame(listener)
    }

    fun createRematch(existingGame: GameOfChess, listener: GameEventListener): RematchDto {
        return RematchDto(
            previousGameId = existingGame.gameId,
            gameOfChess = setupNewGame(listener),
            players = existingGame
                .getPlayers()
                .map { PlayerDto(it.id, Color.of(it.color).opposite().toString()) }
        )
    }

    private fun setupNewGame(listener: GameEventListener): GameOfChess {
        val newGame = GameOfChess("g-" + UUID.randomUUID().toString())
        newGame.setupChessboard(SetupProvider.getSetup(null))
        newGame.registerGameEventListener(listener)
        return newGame
    }
}

data class RematchDto(
    val previousGameId: String,
    val gameOfChess: GameOfChess,
    val players: List<PlayerDto>
) {
    fun opponentPlayerId(id: String) = players.find { it.id != id }?.id ?: players.first().id
    fun colorOf(playerId: String) = players.first { it.id == playerId }.color
}