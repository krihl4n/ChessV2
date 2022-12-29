package com.krihl4n

import com.krihl4n.Command.*
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.GameModeDto.Companion.fromCommand
import com.krihl4n.api.dto.PossibleMovesDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameHandler(private val gameEventSender: GameEventSender) : ConnectionListener {

    private val games: MutableMap<String, GameOfChess> = mutableMapOf()

    fun handleGameCommand(sessionId: String, command: Command) {
        when (command) {
            UNDO_MOVE -> games[sessionId]?.undoMove()
            REDO_MOVE -> games[sessionId]?.redoMove()
            RESIGN -> games[sessionId]?.resign(sessionId)
        }
    }

    override fun connectionEstablished(sessionId: String) {
        println("Register a new game for $sessionId")
        games[sessionId] = GameOfChess(sessionId)
        games[sessionId]?.setupChessboard()
        games[sessionId]?.registerGameEventListener(gameEventSender)
    }

    override fun connectionClosed(sessionId: String) {
        println("Remove game for $sessionId")
        games.remove(sessionId)
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        games[sessionId] = GameOfChess(sessionId)
        games[sessionId]?.setupChessboard()
        games[sessionId]?.registerGameEventListener(gameEventSender)
        games[sessionId]?.requestNewGame(request.playerId, fromCommand(request.mode), request.colorPreference)
        return UUID.randomUUID().toString()
    }

    fun move(sessionId: String, playerId: String, from: String, to: String) {
        games[sessionId]?.move(playerId, from, to)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return games[sessionId]?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return games[sessionId]?.getPossibleMoves(field);
    }
}

enum class Command {
    UNDO_MOVE, REDO_MOVE, RESIGN
}