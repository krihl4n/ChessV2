package com.krihl4n

import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import com.krihl4n.api.dto.GameModeDto.Companion.fromCommand
import com.krihl4n.api.pieceSetups.*
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
    private val gamesRegister: GamesRegister,
    private val rematchManager: RematchManager,
    private val messageSender: MessageSender
) : ConnectionListener {

    override fun connectionEstablished(sessionId: String) {
    }

    override fun connectionClosed(sessionId: String) {
        gamesRegister.deregisterSession(sessionId)
    }

    fun requestNewGame(sessionId: String, request: StartGameRequest): String {
        val newGame = GameOfChess("g-" + UUID.randomUUID().toString()) // TODO generate id inside
        gamesRegister.registerNewGame(newGame, sessionId)
        gamesRegister.getGame(sessionId)?.let {
            it.setupChessboard(SetupProvider.getSetup(request.setup))
            it.registerGameEventListener(gameEventHandler)
            it.requestNewGame(fromCommand(request.mode))
        }
        gamesRegister.debugPrint()
        return newGame.gameId
    }

    fun requestRematch(sessionId: String): String? { // todo what to do with old games?
        val existingGame = gamesRegister.getGame(sessionId)
            ?: return null
        val playerId =
            gamesRegister.getPlayerId(sessionId) ?: throw RuntimeException("No player registered") // todo test

        val opponentPlayerId = existingGame
            .getPlayers()
            .firstOrNull { it.id != playerId }?.id ?: existingGame.getPlayers().first().id
        val opponentSessionId = gamesRegister
            .getRelatedSessionIds(existingGame.gameId)
            .firstOrNull { it != sessionId }
        val playerNextColor = existingGame.getPlayer(playerId)?.color?.let { ColorDto(it.lowercase()) }?.opposite()
            ?: throw RuntimeException("Cannot establish rematch color")

        gamesRegister.deregisterGame(existingGame.gameId)

        val newGame = GameOfChess("g-" + UUID.randomUUID().toString()) // TODO generate id inside
        gamesRegister.registerNewGame(newGame, sessionId)
        gamesRegister.getGame(sessionId)?.let {
            this.rematchManager.createProposal(playerId, opponentPlayerId, playerNextColor, newGame.gameId)
            it.setupChessboard(SetupProvider.getSetup(null))
            it.registerGameEventListener(gameEventHandler)
            it.requestNewGame(existingGame.getMode() ?: GameModeDto.TEST_MODE)
        }

        opponentSessionId?.let { this.messageSender.sendRematchRequestedMsg(opponentSessionId, newGame.gameId) }
        gamesRegister.debugPrint()
        return newGame.gameId
    }

    fun joinGame(sessionId: String, req: JoinGameRequest): String {
        if (!req.rejoin) { // todo separate those cases
            val playerId = req.playerId ?: ("p-" + UUID.randomUUID().toString())
            gamesRegister.registerPlayer(sessionId, req.gameId, playerId)

            val colorPreference = rematchManager
                .getRematchProposal(playerId)
                ?.playerNextColor
                ?.value ?: req.colorPreference

            gamesRegister.getGameById(req.gameId).playerReady(playerId, colorPreference)
            gamesRegister.debugPrint()
            return playerId
        } else {
            gamesRegister.registerPlayer(sessionId, req.gameId, req.playerId!!)
            joinedExistingGame(sessionId, req.gameId, req.playerId)
            gamesRegister.debugPrint()
            return req.playerId
        }
    }

    fun move(sessionId: String, playerId: String, from: String, to: String, pawnPromotion: String?) {
        gamesRegister.getGame(sessionId)?.move(MoveDto(playerId, from, to, pawnPromotion))
    }

    fun getPositions(sessionId: String): List<FieldOccupationDto>? {
        return gamesRegister.getGame(sessionId)?.getFieldOccupationInfo()
    }

    fun getPossibleMoves(sessionId: String, field: String): PossibleMovesDto? {
        return gamesRegister.getGame(sessionId)?.getPossibleMoves(field)
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

    private fun joinedExistingGame(sessionId: String, gameId: String, playerId: String) {
        val game: GameOfChess = gamesRegister.getGameById(gameId)
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
