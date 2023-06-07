package com.krihl4n

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.GameOfChessCreator
import com.krihl4n.api.dto.*
import com.krihl4n.app.ConnectionListener
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.StartGameRequest
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*

@Service
class GameCommandHandler(
    private val gameEventHandler: GameEventHandler,
    private val register: GamesRegister,
    private val rematchManager: RematchManager,
    private val messageSender: MessageSender
) : ConnectionListener {

    override fun connectionEstablished(sessionId: String) {
    }

    override fun connectionClosed(sessionId: String) {
        register.deregisterSession(sessionId)
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = GameOfChessCreator
            .createGame(request.setup, gameEventHandler)
            .also {
                register.registerNewGame(it, sessionId)
                it.initialize(request.mode)
            }
        register.debugPrint()
        return newGame.gameId
    }

    fun requestRematch(sessionId: String): String? { // todo what to do with old games?
        val existingGame = register.getGame(sessionId)
            ?: return null
        if (this.rematchManager.proposalExists(existingGame.gameId)) {
            return null
        }
        val rematch = GameOfChessCreator.createRematch(existingGame, gameEventHandler)
        val playerId = register.getPlayerId(sessionId) ?: throw RuntimeException("No player registered") // todo test
        val newGame = rematch.gameOfChess.also {
            register.registerNewGame(it, sessionId)
            it.initialize(existingGame.getMode())
        }
        this.rematchManager.createProposal(
            playerId,
            rematch.opponentPlayerId(playerId),
            ColorDto(rematch.colorOf(playerId)),
            newGame.gameId,
            existingGame.gameId
        )

        register
            .getRelatedSessionIds(existingGame.gameId)
            .firstOrNull { it != sessionId }
            ?.let { this.messageSender.sendRematchRequestedMsg(it, newGame.gameId) }
        register.deregisterGame(existingGame.gameId)
        register.debugPrint()
        return newGame.gameId
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        if (!req.rejoin) { // todo separate those cases
            val playerId = req.playerId ?: ("p-" + UUID.randomUUID().toString())
            register.registerPlayer(sessionId, req.gameId, playerId)

            val colorPreference = rematchManager
                .getRematchProposal(playerId)
                ?.playerNextColor
                ?.value ?: req.colorPreference

            register.getGameById(req.gameId).playerReady(playerId, colorPreference)
            register.debugPrint()
            return playerId
        } else {
            register.registerPlayer(sessionId, req.gameId, req.playerId!!)
            joinedExistingGame(sessionId, req.gameId, req.playerId)
            register.debugPrint()
            return req.playerId
        }
    }

    fun move(sessionId: String, playerId: String, from: String, to: String, pawnPromotion: String?) {
        register.getGame(sessionId)?.move(MoveDto(playerId, from, to, pawnPromotion))
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return register.getGame(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return register.getGame(sessionId)?.getPossibleMoves(field)
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
        val game: GameOfChess = register.getGameById(gameId)
        game.getPlayer(playerId)?.let {
            val gameInfo = GameInfoEvent(
                gameId = gameId,
                mode = "", // todo needed?
                player = PlayerDto(playerId, it.color),
                piecePositions = game.getFieldOccupationInfo(),
                turn = game.getColorAllowedToMove()
            )
            messageSender.sendJoinedExistingGameMsg(sessionId, gameInfo)
        }
    }
}
