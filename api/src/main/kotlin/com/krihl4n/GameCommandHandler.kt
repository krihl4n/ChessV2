package com.krihl4n

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.GameModeDto.Companion.fromCommand
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.app.ConnectionListener
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameCommandHandler(
    private val gameEventHandler: GameEventHandler,
    private val gamesRegister: GamesRegister,
) : ConnectionListener {

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = GameOfChess(UUID.randomUUID().toString()) // TODO generate id inside
        gamesRegister.reqisterNewGame(newGame, sessionId)
        gamesRegister.getGame(sessionId)?.setupChessboard() // TODO just use newGame object?
        gamesRegister.getGame(sessionId)?.registerGameEventListener(gameEventHandler)
        gamesRegister.getGame(sessionId)?.requestNewGame(fromCommand(request.mode))
        return newGame.gameId
    }

    fun move(sessionId: String, playerId: String, from: String, to: String) {
        gamesRegister.getGame(sessionId)?.move(playerId, from, to)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return gamesRegister.getGame(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return gamesRegister.getGame(sessionId)?.getPossibleMoves(field)
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        val playerId = UUID.randomUUID().toString()
        gamesRegister.joinPlayer(sessionId, playerId)
        gamesRegister.joinGame(sessionId, req.gameId)
        if (req.playerId == null) {
            gamesRegister.getGameById(req.gameId).playerReady(playerId, req.colorPreference)
        } else {
            gameEventHandler.joinedExistingGame(sessionId, req.gameId, req.playerId)
        }
        return playerId
    }

    override fun connectionEstablished(sessionId: String) {
    }

    override fun connectionClosed(sessionId: String) {
        gamesRegister.deregisterSession(sessionId)
    }

    fun resign(sessionId: String, playerId: String) {
        gamesRegister.getGame(sessionId)?.resign(playerId)
    }

    // todo this needs to be smarter
    fun undoMove(sessionId: String, playerId: String) {
        gamesRegister.getGame(sessionId)?.undoMove()
    }

    fun redoMove(sessionId: String, playerId: String) {
        gamesRegister.getGame(sessionId)?.redoMove()
    }
}
