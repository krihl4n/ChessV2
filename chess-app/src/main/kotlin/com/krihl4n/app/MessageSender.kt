package com.krihl4n.app

import com.krihl4n.api.dto.*
import com.krihl4n.app.messages.GameInfoEvent
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
        sendMsg(sessionId, "/queue/piece-position-updates", update)
    }

    fun sendGameStateUpdateMsg(sessionId: String, update: GameStateUpdateDto) {
        sendMsg(sessionId, "/queue/game-state-updates", update)
    }

    fun sendGameStartedMsg(sessionId: String, gameInfo: GameInfoEvent) {
        sendMsg(sessionId, "/queue/game-started", gameInfo)
    }

    fun sendGameFinishedMsg(sessionId: String, result: GameResultDto) {
        sendMsg(sessionId, "/queue/game-result", result)
    }

    fun sendWaitingForOtherPlayerMsg(sessionId: String, gameId:String) {
        sendMsg(sessionId, "/queue/waiting-for-other-player", gameId)
    }

    fun sendJoinedExistingGameMsg(sessionId: String, gameInfo: GameInfoEvent) {
        sendMsg(sessionId, "/queue/joined-existing-game", gameInfo)
    }

    fun sendRematchRequestedMsg(sessionId: String, gameId: String) {
        sendMsg(sessionId, "/queue/rematch-requested", gameId)
    }

    private fun sendMsg(sessionId: String, destination: String, result: Any) {
        println("<-- $sessionId | $destination | $result")
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