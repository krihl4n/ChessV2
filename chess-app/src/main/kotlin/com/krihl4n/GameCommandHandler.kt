package com.krihl4n

import com.krihl4n.api.dto.*
import com.krihl4n.app.ConnectionListener
import com.krihl4n.app.MessageSender
import com.krihl4n.app.messages.GameInfoEvent
import com.krihl4n.app.messages.JoinGameRequest
import com.krihl4n.app.messages.RejoinGameRequest
import com.krihl4n.app.messages.StartGameRequest
import com.krihl4n.gamesManagement.GameOfChessCreator
import com.krihl4n.gamesManagement.RematchProposals
import com.krihl4n.gamesManagement.SessionRegistry
import com.krihl4n.persistence.GamesRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameCommandHandler(
    private val registry: SessionRegistry,
    private val rematchProposals: RematchProposals,
    private val messageSender: MessageSender,
    private val gameOfChessCreator: GameOfChessCreator,
    private val gamesRepository: GamesRepository
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
        registry.registerNewGame(newGame.gameId, sessionId)
        gamesRepository.saveNewGame(newGame)
        gamesRepository.getGameForCommand(newGame.gameId).initialize()
        return newGame.gameId
    }

    fun requestRematch(sessionId: String, gameId: String): String? { // todo what to do with old games?
        val existingGame = gamesRepository.getGameForQuery(gameId)
        if (this.rematchProposals.proposalExists(existingGame.gameId())) {
            return null
        }
        val rematch = gameOfChessCreator.createRematch(existingGame)
        val newGame = rematch.gameOfChess.also {
            registry.registerNewGame(it.gameId, sessionId)
            gamesRepository.saveNewGame(it)
        }
        this.rematchProposals.createProposal(rematch)
        gamesRepository.getGameForCommand(newGame.gameId).initialize()
        registry
            .getRelatedSessionIds(existingGame.gameId())
            .firstOrNull { it != sessionId }
            ?.let { this.messageSender.sendRematchRequestedMsg(it, newGame.gameId) }
        registry.deregisterGame(existingGame.gameId())
        return newGame.gameId
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        val playerId = req.playerId ?: (UUID.randomUUID().toString())
        registry.registerPlayer(sessionId, req.gameId, playerId)

        val colorPreference = rematchProposals
            .getRematchProposal(playerId)
            ?.playerNextColor
            ?: req.colorPreference

        gamesRepository.getGameForCommand(req.gameId).playerReady(playerId, colorPreference)
        return playerId
    }

    fun rejoinGame(sessionId: String, req: RejoinGameRequest) {
        registry.registerPlayer(sessionId, req.gameId, req.playerId)
        joinedExistingGame(sessionId, req.gameId, req.playerId)
    }

    fun move(gameId: String, move: MoveDto) {
        gamesRepository.getGameForCommand(gameId).move(move)
    }

    fun getPositions(gameId: String): List<FieldOccupationDto> {
        return gamesRepository.getGameForQuery(gameId).getFieldOccupationInfo()
    }

    fun getPossibleMoves(gameId: String, field: String): PossibleMovesDto {
        return gamesRepository.getGameForQuery(gameId).getPossibleMoves(field)
    }

    fun resign(gameId: String, playerId: String) {
        gamesRepository.getGameForCommand(gameId).resign(playerId)
    }

    fun undoMove(gameId: String, playerId: String) {
        gamesRepository.getGameForCommand(gameId).undoMove(playerId)
    }

    private fun joinedExistingGame(sessionId: String, gameId: String, playerId: String) {
        val game = gamesRepository.getGameForQuery(gameId)
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
