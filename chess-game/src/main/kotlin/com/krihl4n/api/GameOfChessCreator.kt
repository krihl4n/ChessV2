package com.krihl4n.api

import com.krihl4n.api.dto.PlayerDto
import com.krihl4n.api.pieceSetups.SetupProvider
import com.krihl4n.model.Color
import java.util.*

object GameOfChessCreator {

    fun createGame(mode: String, setup: String?, listener: GameEventListener): GameOfChess { // todo listener moze nie byc potrzebny
        return setupNewGame(mode, setup, listener)
    }

    fun createRematch(existingGame: GameOfChess, listener: GameEventListener): RematchDto {
        return RematchDto(
            previousGameId = existingGame.gameId,
            gameOfChess = setupNewGame(existingGame.getMode(), null, listener),
            players = existingGame
                .getPlayers()
                .map { PlayerDto(it.id, Color.of(it.color).opposite().toString()) }
        )
    }

    private fun setupNewGame(mode: String, setup: String?, listener: GameEventListener): GameOfChess {
        val newGame = GameOfChess("g-" + UUID.randomUUID().toString(), mode, SetupProvider.getSetup(setup))
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