package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.ColorDto
import com.krihl4n.api.dto.PlayerDto
import com.krihl4n.api.pieceSetups.SetupProvider
import java.util.*

object GameOfChessCreator {

    fun createGame(mode: String, setup: String?, listeners: List<GameEventListener>): GameOfChess { // todo listener moze nie byc potrzebny
        return setupNewGame(mode, setup, listeners)
    }

    fun createRematch(existingGame: GameOfChess, listeners: List<GameEventListener>): RematchDto {
        return RematchDto(
            previousGameId = existingGame.gameId,
            gameOfChess = setupNewGame(existingGame.getMode(), null, listeners),
            players = existingGame
                .getPlayers()
                .map { PlayerDto(it.id, ColorDto(it.color).opposite().value) }
        )
    }

    private fun setupNewGame(mode: String, setup: String?, listeners: List<GameEventListener>): GameOfChess {
        val newGame = GameOfChess(UUID.randomUUID().toString(), mode, SetupProvider.getSetup(setup))
        listeners.forEach{ newGame.registerGameEventListener(it)}
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