package com.krihl4n

import com.krihl4n.api.GameEventListener
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class GameEventSender(
    private val simpMessagingTemplate: SimpMessagingTemplate
) : GameEventListener {

    override fun pieceMoved(sessionId: String, from: String, to: String) {
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            "/user/queue/moves",
            Move(from, to),
            prepareSessionIdHeader(sessionId))
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }
}