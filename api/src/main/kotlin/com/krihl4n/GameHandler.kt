package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.GameModeDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.api.pieceSetups.AboutToCheckMateSetup
import org.springframework.stereotype.Service

@Service
class GameHandler(private val gameEventSender: GameEventSender) : ConnectionListener {

    private val games: MutableMap<String, GameOfChess> = mutableMapOf()

    fun handleGameCommand(sessionId: String, command: Command) {
        when (command) {
            Command.START_FREE_MODE -> games[sessionId]?.start(sessionId, GameModeDto.fromCommand(Command.START_FREE_MODE.toString()))
            Command.START_HOT_SEAT -> games[sessionId]?.start(sessionId, GameModeDto.fromCommand(Command.START_HOT_SEAT.toString()))
            Command.START_VS_COMPUTER -> games[sessionId]?.start(sessionId, GameModeDto.fromCommand(Command.START_VS_COMPUTER.toString()))
            Command.UNDO_MOVE -> games[sessionId]?.undoMove()
            Command.REDO_MOVE -> games[sessionId]?.redoMove()
        }
    }

    override fun connectionEstablished(sessionId: String) {
        println("Register a new game for $sessionId")
        games[sessionId] = GameOfChess(sessionId)
        games[sessionId]?.setupChessboard(AboutToCheckMateSetup())
        games[sessionId]?.registerGameEventListener(gameEventSender)
    }

    override fun connectionClosed(sessionId: String) {
        println("Remove game for $sessionId")
        games.remove(sessionId)
    }

    fun move(sessionId: String, from: String, to: String) {
        games[sessionId]?.move(sessionId, from, to)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return games[sessionId]?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return games[sessionId]?.getPossibleMoves(field);
    }
}

enum class Command {
    START_FREE_MODE, START_HOT_SEAT, START_VS_COMPUTER, UNDO_MOVE, REDO_MOVE
}