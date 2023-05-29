package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import org.springframework.stereotype.Service

@Service
class GameEventHandler(
    private val messageSender: MessageSender,
    private val gamesRegister: GamesRegister,
    private val rematchManager: RematchManager
) : GameEventListener {

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        getSessionIds(gameId).forEach { messageSender.sendPiecePositionUpdateMsg(it, update) }
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        getSessionIds(gameId).forEach { messageSender.sendGameStateUpdateMsg(it, update) }
        if (update.gameState == "WAITING_FOR_PLAYERS") {
            getSessionIds(gameId).forEach {
                messageSender.sendWaitingForOtherPlayerMsg(
                    it,
                    gameId
                )
            }
        }
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        gamesRegister.getRelatedPlayerSessionId(gameInfo.player1.id)?.let {
            messageSender.sendGameStartedMsg(
                it,
                GameInfoEvent(
                    gameInfo.gameId,
                    gameInfo.mode,
                    gameInfo.player1,
                    gameInfo.piecePositions,
                    gameInfo.turn
                )
            )
        }
        if(gameInfo.player1 == gameInfo.player2) {
            return
        }
        gamesRegister.getRelatedPlayerSessionId(gameInfo.player2.id)?.let {
            messageSender.sendGameStartedMsg(
                it,
                GameInfoEvent(
                    gameInfo.gameId,
                    gameInfo.mode,
                    gameInfo.player2,
                    gameInfo.piecePositions,
                    gameInfo.turn
                )
            )
        }
        rematchManager.clearProposals(gameInfo.gameId) // todo separate listener?
    }
    override fun gameFinished(gameId: String, result: GameResultDto) {
        getSessionIds(gameId).forEach { messageSender.sendGameFinishedMsg(it, result) }
    }

    fun joinedExistingGame(sessionId: String, gameId: String, playerId: String) {
        val game: GameOfChess = gamesRegister.getGameById(gameId)
        game.getPlayer(playerId)?.let {
            val gameInfo = GameInfoEvent(
                gameId = gameId,
                mode = "", // todo needed?
                player = PlayerDto(playerId, it.color.toString()),
                piecePositions = game.getFieldOccupationInfo(),
                turn = game.getColorAllowedToMove()
            )
            messageSender.sendJoinedExistingGameMsg(sessionId, gameInfo)
        }
    }

    fun rematchRequested(sessionId: String, gameId: String) {
        messageSender.sendRematchRequestedMsg(sessionId, gameId)
    }

    private fun getSessionIds(gameId: String) = gamesRegister.getRelatedSessionIds(gameId)
}