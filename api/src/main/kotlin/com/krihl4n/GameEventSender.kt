package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import org.springframework.stereotype.Service

@Service
class GameEventSender(
    private val messageSender: MessageSender,
    private val gamesRegister: GamesRegister
) : GameEventListener {

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        messageSender.sendPiecePositionUpdateMsg(getSessionId(gameId), update)
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        messageSender.sendGameStateUpdateMsg(getSessionId(gameId), update)
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        messageSender.sendGameStartedMsg(getSessionId(gameId), gameInfo)
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        messageSender.sendGameFinishedMsg(getSessionId(gameId), result)
    }

    private fun getSessionId(gameId: String) = gamesRegister.getRelatedSessionIds(gameId).first() // TODO multiple
}