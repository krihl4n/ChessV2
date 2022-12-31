package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameEventSender(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val gamesRegister: GamesRegister
) : GameEventListener {

    override fun piecePositionUpdate(gameId: String, update: PiecePositionUpdateDto) {
        simpMessagingTemplate.convertAndSendToUser(
            gamesRegister.getRelatedSessionIds(gameId).first(), // todo notify many
            "user/queue/piece-position-updates",
            update,
            prepareSessionIdHeader(gamesRegister.getRelatedSessionIds(gameId).first())
        )
    }

    override fun gameStateUpdate(gameId: String, update: GameStateUpdateDto) {
        simpMessagingTemplate.convertAndSendToUser(
            gamesRegister.getRelatedSessionIds(gameId).first(),
            "/user/queue/game-state-updates",
            update,
            prepareSessionIdHeader(gamesRegister.getRelatedSessionIds(gameId).first())
        )
    }

    override fun gameStarted(gameId: String, gameInfo: GameInfoDto) {
        simpMessagingTemplate.convertAndSendToUser(
            gamesRegister.getRelatedSessionIds(gameId).first(),
            "/user/queue/game-started",
            gameInfo,
            prepareSessionIdHeader(gamesRegister.getRelatedSessionIds(gameId).first())
        )
    }

    override fun gameFinished(gameId: String, result: GameResultDto) {
        simpMessagingTemplate.convertAndSendToUser(
            gamesRegister.getRelatedSessionIds(gameId).first(),
            "/user/queue/game-result",
            result,
            prepareSessionIdHeader(gamesRegister.getRelatedSessionIds(gameId).first())
        )
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }
}