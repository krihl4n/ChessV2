package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.app.MessageSender
import com.krihl4n.events.GameInfoEvent
import org.springframework.stereotype.Service

@Service
class GameEventHandler(
    private val messageSender: MessageSender,
    private val gamesRegister: GamesRegister,
    private val joinGameHandler: JoinGameHandler,
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
                    joinGameHandler.generateJoinCode(gameId)
                )
            }
        }
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        this.joinGameHandler.notifyGameStarted(gameId)
        getSessionIds(gameId).forEach {
            messageSender.sendGameStartedMsg(
                it,
                GameInfoEvent(
                    gameInfo.gameId,
                    gameInfo.mode,
                    gameInfo.player1,
                    gameInfo.piecePositions
                )
            )
        }
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        getSessionIds(gameId).forEach { messageSender.sendGameFinishedMsg(it, result) }
    }

    private fun getSessionIds(gameId: String) = gamesRegister.getRelatedSessionIds(gameId)
}