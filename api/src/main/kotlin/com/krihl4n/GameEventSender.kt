package com.krihl4n

import com.krihl4n.api.GameEventListener
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
        println("sending piece position update")
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            "user/queue/piece-position-updates",
            update,
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