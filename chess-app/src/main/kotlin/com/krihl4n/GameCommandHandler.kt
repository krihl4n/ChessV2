package com.krihl4n

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.GameOfChessCreator
import com.krihl4n.api.dto.*
import com.krihl4n.app.ConnectionListener
import com.krihl4n.app.MessageSender
import com.krihl4n.messages.GameInfoEvent
import com.krihl4n.messages.JoinGameRequest
import com.krihl4n.messages.RejoinGameRequest
import com.krihl4n.messages.StartGameRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameCommandHandler(
    private val gameEventHandler: GameEventHandler,
    private val register: GamesRegister,
    private val rematchProposals: RematchProposals,
    private val messageSender: MessageSender,
) : ConnectionListener {

    override fun connectionEstablished(sessionId: String) {
        // todo send rejoin on connection established from frontend?
    }

    override fun connectionClosed(sessionId: String) {
        register.deregisterSession(sessionId)
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = GameOfChessCreator
            .createGame(request.mode, request.setup, gameEventHandler)
            .also {
                register.registerNewGame(it, sessionId)
            }
        register.getGameForCommand(newGame.gameId)?.let {
            it.registerGameEventListener(gameEventHandler)
            it.initialize()
        }
        return newGame.gameId
    }

    fun requestRematch(sessionId: String): String? { // todo what to do with old games?
        val existingGame = register.getGameForQuery(sessionId)
            ?: return null
        if (this.rematchProposals.proposalExists(existingGame.gameId)) {
            return null
        }
        val rematch = GameOfChessCreator.createRematch(existingGame, gameEventHandler)
        val newGame = rematch.gameOfChess.also {
            register.registerNewGame(it, sessionId)
        }
        register.getGameForCommand(newGame.gameId)?.let {
            it.registerGameEventListener(gameEventHandler)
            it.initialize()
        }
        this.rematchProposals.createProposal(rematch)

        register
            .getRelatedSessionIds(existingGame.gameId)
            .firstOrNull { it != sessionId }
            ?.let { this.messageSender.sendRematchRequestedMsg(it, newGame.gameId) }
        register.deregisterGame(existingGame.gameId)
        return newGame.gameId
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        val playerId = req.playerId ?: (UUID.randomUUID().toString())
        register.registerPlayer(sessionId, req.gameId, playerId)

        val colorPreference = rematchProposals
            .getRematchProposal(playerId)
            ?.playerNextColor
            ?: req.colorPreference

        register.getGameForCommand(req.gameId)?.let {
            it.registerGameEventListener(gameEventHandler)
            it.playerReady(playerId, colorPreference)
        }
        return playerId
    }

    fun rejoinGame(sessionId: String, req: RejoinGameRequest) {
        register.registerPlayer(sessionId, req.gameId, req.playerId)
        joinedExistingGame(sessionId, req.gameId, req.playerId)
    }

    fun move(sessionId: String, playerId: String, from: String, to: String, pawnPromotion: String?) {
        register.getGame(sessionId)?.let {
            it.registerGameEventListener(gameEventHandler)
            it.move(MoveDto(playerId, from, to, pawnPromotion))
        }
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? { // todo all commands by game id, not session id
        return register.getGameForQuery(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return register.getGameForQuery(sessionId)?.getPossibleMoves(field)
    }

    fun resign(sessionId: String, playerId: String) {
        register.getGame(sessionId)?.resign(playerId)
    }

    // todo this needs to be smarter
    fun undoMove(sessionId: String, playerId: String) {
        register.getGame(sessionId)?.undoMove()
    }

    fun redoMove(sessionId: String, playerId: String) {
        register.getGame(sessionId)?.redoMove()
    }

    private fun joinedExistingGame(sessionId: String, gameId: String, playerId: String) {
        val game: GameOfChess = register.getGameForQueryById(gameId)
        game.getPlayer(playerId)?.let {
            val gameInfo = GameInfoEvent(
                gameId = gameId,
                mode = game.getMode(),
                player = PlayerDto(playerId, it.color),
                piecePositions = game.getFieldOccupationInfo(),
                turn = game.getColorAllowedToMove()
            )
            messageSender.sendJoinedExistingGameMsg(sessionId, gameInfo)
        }
    }
}
