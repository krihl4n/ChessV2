package com.krihl4n.gamesManagement

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.ColorDto
import com.krihl4n.api.dto.PlayerDto
import com.krihl4n.api.pieceSetups.SetupProvider
import com.krihl4n.persistence.ReadOnlyGameOfChess
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameOfChessCreator {

    private val gameListeners = mutableListOf<GameEventListener>()

    fun registerNewGameObserver(gameEventListener: GameEventListener) {
        gameListeners.add(gameEventListener)
    }

    fun createGame(mode: String, setup: String?): GameOfChess {
        return setupNewGame(mode, setup)
    }

    fun createRematch(existingGame: ReadOnlyGameOfChess): RematchDto {
        return RematchDto(
            previousGameId = existingGame.gameId(),
            gameOfChess = setupNewGame(existingGame.getMode(), null),
            players = existingGame
                .getPlayers()
                .map { PlayerDto(it.id, ColorDto(it.color).opposite().value) }
        )
    }

    fun createWithoutListeners(id: String, mode: String): GameOfChess {
        return GameOfChess(id, mode, null)
    }

    fun registerListeners(gameOfChess: GameOfChess) {
        gameListeners.forEach{ gameOfChess.registerGameEventListener(it)}
    }

    private fun setupNewGame(mode: String, setup: String?): GameOfChess {
        val newGame = GameOfChess(UUID.randomUUID().toString(), mode, SetupProvider.getSetup(setup))
        gameListeners.forEach{ newGame.registerGameEventListener(it)}
        return newGame
    }
}

data class RematchDto(
    val previousGameId: String,
    val gameOfChess: GameOfChess,
    val players: List<PlayerDto>
)
