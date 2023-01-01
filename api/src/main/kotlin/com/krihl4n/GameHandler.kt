package com.krihl4n

import com.krihl4n.Command.*
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.GameModeDto.Companion.fromCommand
import com.krihl4n.api.dto.PossibleMovesDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameHandler(
    private val gameEventSender: GameEventSender,
    private val gamesRegister: GamesRegister
): ConnectionListener {

    fun handleGameCommand(sessionId: String, command: Command) {
        when (command) {
            UNDO_MOVE -> gamesRegister.getGame(sessionId)?.undoMove()
            REDO_MOVE -> gamesRegister.getGame(sessionId)?.redoMove()
            RESIGN -> gamesRegister.getGame(sessionId)?.resign(sessionId)
        }
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = GameOfChess(UUID.randomUUID().toString())
        gamesRegister.reqisterNewGame(newGame, sessionId)
        gamesRegister.getGame(sessionId)?.setupChessboard()
        gamesRegister.getGame(sessionId)?.registerGameEventListener(gameEventSender)
        gamesRegister.getGame(sessionId)?.requestNewGame(request.playerId, fromCommand(request.mode), request.colorPreference)
        return newGame.gameId
    }

    fun move(sessionId: String, playerId: String, from: String, to: String) {
        gamesRegister.getGame(sessionId)?.move(playerId, from, to)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return gamesRegister.getGame(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return gamesRegister.getGame(sessionId)?.getPossibleMoves(field);
    }

    override fun connectionEstablished(sessionId: String) {
    }

    override fun connectionClosed(sessionId: String) {
        gamesRegister.deregisterSession(sessionId)
    }
}

enum class Command {
    UNDO_MOVE, REDO_MOVE, RESIGN
}