package com.krihl4n

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import com.krihl4n.app.ConnectionListener
import com.krihl4n.app.MessageSender
import com.krihl4n.messages.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameCommandHandler(
    private val registry: GamesRegistry,
    private val rematchProposals: RematchProposals,
    private val messageSender: MessageSender,
    private val gameOfChessCreator: GameOfChessCreator
) : ConnectionListener {

    override fun connectionEstablished(sessionId: String) {
        // todo send rejoin on connection established from frontend?
    }

    override fun connectionClosed(sessionId: String) {
        registry.deregisterSession(sessionId)
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = gameOfChessCreator
            .createGame(request.mode, request.setup)
        registry.registerNewGame(newGame, sessionId)
        registry.getGameForCommand(newGame.gameId)?.initialize()
        return newGame.gameId
    }

    fun requestRematch(sessionId: String, gameId: String): String? { // todo what to do with old games?
        val existingGame = registry.getGameForQueryById(gameId)
        if (this.rematchProposals.proposalExists(existingGame.gameId)) {
            return null
        }
        val rematch = gameOfChessCreator.createRematch(existingGame)
        val newGame = rematch.gameOfChess.also {
            registry.registerNewGame(it, sessionId)
        }
        this.rematchProposals.createProposal(rematch)
        registry.getGameForCommand(newGame.gameId)?.initialize()
        registry
            .getRelatedSessionIds(existingGame.gameId)
            .firstOrNull { it != sessionId }
            ?.let { this.messageSender.sendRematchRequestedMsg(it, newGame.gameId) }
        registry.deregisterGame(existingGame.gameId)
        return newGame.gameId
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        val playerId = req.playerId ?: (UUID.randomUUID().toString())
        registry.registerPlayer(sessionId, req.gameId, playerId)

        val colorPreference = rematchProposals
            .getRematchProposal(playerId)
            ?.playerNextColor
            ?: req.colorPreference

        registry.getGameForCommand(req.gameId)?.playerReady(playerId, colorPreference)
        return playerId
    }

    fun rejoinGame(sessionId: String, req: RejoinGameRequest) {
        registry.registerPlayer(sessionId, req.gameId, req.playerId)
        joinedExistingGame(sessionId, req.gameId, req.playerId)
    }

    fun move(gameId: String, move: MoveDto) {
        registry.getGameForCommand(gameId)?.move(move)
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? { // todo all commands by game id, not session id
        return registry.getGameForQuery(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return registry.getGameForQuery(sessionId)?.getPossibleMoves(field)
    }

    fun resign(sessionId: String, playerId: String) {
        registry.getGame(sessionId)?.resign(playerId)
    }

    // todo this needs to be smarter
    fun undoMove(sessionId: String, playerId: String) {
        registry.getGame(sessionId)?.undoMove()
    }

    fun redoMove(sessionId: String, playerId: String) {
        registry.getGame(sessionId)?.redoMove()
    }

    private fun joinedExistingGame(sessionId: String, gameId: String, playerId: String) {
        val game: GameOfChess = registry.getGameForQueryById(gameId)
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
