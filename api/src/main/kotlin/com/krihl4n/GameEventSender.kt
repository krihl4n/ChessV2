package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameEventSender(
    private val simpMessagingTemplate: SimpMessagingTemplate
) : GameEventListener {

    override fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto) {
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            "user/queue/piece-position-updates",
            update,
            prepareSessionIdHeader(sessionId)
        )
    }

    override fun gameStateUpdate(sessionId: String, update: GameStateUpdateDto) {
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            "/user/queue/game-state-updates",
            update,
            prepareSessionIdHeader(sessionId)
        )
    }

    override fun gameFinished(sessionId: String, result: GameResultDto) {
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            "/user/queue/game-result",
            result,
            prepareSessionIdHeader(sessionId)
        )
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }
}