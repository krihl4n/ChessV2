package com.krihl4n.app

import com.krihl4n.api.dto.GameInfoDto
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class MessageSender(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {

    fun sendPiecePositionUpdateMsg(sessionId: String, update: PiecePositionUpdateDto) {
        sendMsg(sessionId, "user/queue/piece-position-updates", update)
    }

    fun sendGameStateUpdateMsg(sessionId: String, update: GameStateUpdateDto) {
        sendMsg(sessionId, "/user/queue/game-state-updates", update)
    }

    fun sendGameStartedMsg(sessionId: String, gameInfo: GameInfoDto) {
        sendMsg(sessionId, "/user/queue/game-started", gameInfo)
    }

    fun sendGameFinishedMsg(sessionId: String, result: GameResultDto) {
        sendMsg(sessionId, "/user/queue/game-result", result)
    }

    fun sendWaitingForOtherPlayerMsg(sessionId: String, joinToken:String) {
        sendMsg(sessionId, "user/queue/waiting-for-other-player", joinToken)
    }

    private fun sendMsg(sessionId: String, destination: String, result: Any) {
        simpMessagingTemplate.convertAndSendToUser(
            sessionId,
            destination,
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