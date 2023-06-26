package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.*
import com.krihl4n.app.MessageSender
import com.krihl4n.messages.GameInfoEvent
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class GameEventHandler(
    private val messageSender: MessageSender,
    private val sessionRegistry: SessionRegistry,
    private val rematchProposals: RematchProposals,
    private val gameOfChessCreator: GameOfChessCreator
) : GameEventListener {

    @PostConstruct
    fun post() {
        gameOfChessCreator.registerNewGameObserver(this)
    }

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        getSessionIds(gameId).forEach { messageSender.sendPiecePositionUpdateMsg(it, update) }
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        getSessionIds(gameId).forEach { messageSender.sendGameStateUpdateMsg(it, update) }
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        rematchProposals.clearProposals(gameInfo.gameId)

        for (player in setOf(gameInfo.player1, gameInfo.player2)) {
            sessionRegistry.getRelatedPlayerSessionId(player.id)?.let {
                messageSender.sendGameStartedMsg(
                    it,
                    GameInfoEvent(
                        gameInfo.gameId,
                        gameInfo.mode,
                        player,
                        gameInfo.piecePositions,
                        gameInfo.turn
                    )
                )
            }
        }
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        getSessionIds(gameId).forEach { messageSender.sendGameFinishedMsg(it, result) }
    }

    override fun waitingForOtherPlayer(gameId: String) {
        getSessionIds(gameId).forEach { messageSender.sendWaitingForOtherPlayerMsg(it, gameId) }
    }

    private fun getSessionIds(gameId: String) = sessionRegistry.getRelatedSessionIds(gameId)
}