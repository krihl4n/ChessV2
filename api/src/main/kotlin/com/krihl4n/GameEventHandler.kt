package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import org.springframework.stereotype.Service

@Service
class GameEventHandler(
    private val messageSender: MessageSender,
    private val gamesRegister: GamesRegister,
) : GameEventListener {

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        getSessionId(gameId)?.let { messageSender.sendPiecePositionUpdateMsg(it, update) }
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        getSessionId(gameId)?.let { messageSender.sendGameStateUpdateMsg(it, update) }
        if (update.gameState == "WAITING_FOR_PLAYERS") {
            getSessionId(gameId)?.let { messageSender.sendWaitingForOtherPlayerMsg(it, "7777") }
        }
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        getSessionId(gameId)?.let { messageSender.sendGameStartedMsg(it, gameInfo) }
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        getSessionId(gameId)?.let { messageSender.sendGameFinishedMsg(it, result) }
    }

    private fun getSessionId(gameId: String) = gamesRegister.getRelatedSessionIds(gameId).firstOrNull() // TODO multiple
}