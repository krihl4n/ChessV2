package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.pieceSetups.CastlingPieceSetup
import com.krihl4n.api.pieceSetups.EnPassantSetup
import com.krihl4n.api.pieceSetups.QueenConversionSetup
import com.krihl4n.api.pieceSetups.SimpleAttackSetup
import org.springframework.stereotype.Service

@Service
class GameHandler(private val gameEventSender: GameEventSender) : ConnectionListener {

    private val games: MutableMap<String, GameOfChess> = mutableMapOf()

    fun handleGameCommand(sessionId: String, command: Command) {
        when (command) {
            Command.START_GAME -> games[sessionId]?.start()
            Command.UNDO_MOVE -> games[sessionId]?.undoMove()
            Command.REDO_MOVE -> games[sessionId]?.redoMove()
        }
    }

    override fun connectionEstablished(sessionId: String) {
        println("Register a new game for $sessionId")
        games[sessionId] = GameOfChess(sessionId)
        games[sessionId]?.setupChessboard(QueenConversionSetup())
        games[sessionId]?.registerGameEventListener(gameEventSender)
    }

    override fun connectionClosed(sessionId: String) {
        println("Remove game for $sessionId")
        games.remove(sessionId)
    }

    fun move(sessionId: String, from: String, to: String) {
        games[sessionId]?.move(from, to)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return games[sessionId]?.getFieldOccupationInfo()
    }
}

enum class Command {
    START_GAME, UNDO_MOVE, REDO_MOVE
}