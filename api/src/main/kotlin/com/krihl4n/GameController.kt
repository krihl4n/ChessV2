package com.krihl4n

import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class GameController(
    private val gameHandler: GameHandler,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {

    // https://medium.com/swlh/websockets-with-spring-part-3-stomp-over-websocket-3dab4a21f397 todo subscribe, error handling
    // https://www.baeldung.com/spring-websockets-sendtouser

    @MessageMapping("/move")
    @Throws(Exception::class)
    fun move(@Payload move: Move, @Header("simpSessionId") sessionId: String) {
        println(move)
        gameHandler.move(sessionId, move.from, move.to)
    }

    @MessageMapping("/game-controls")
    @Throws(Exception::class)
    fun gameControls(@Payload command: String, @Header("simpSessionId") sessionId: String) {
        gameHandler.handleGameCommand(sessionId, Command.valueOf(command.uppercase()))
    }

    @MessageMapping("/start-new-game") // request-new-game
    @Throws(Exception::class)
    fun startNewGame(@Payload startGameRequest: StartGameRequest, @Header("simpSessionId") sessionId: String) {
        gameHandler.requestNewGame(sessionId, startGameRequest)
    }

    @MessageMapping("/fields-occupation")
    @Throws(Exception::class)
    fun fieldsOccupation(@Payload command: String, @Header("simpSessionId") sessionId: String) {
        gameHandler.getPositions(sessionId)?.let {
            simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/user/queue/fields-occupation",
                it,
                prepareSessionIdHeader(sessionId)
            )
        }
    }

    @MessageMapping("/possible-moves")
    @Throws(Exception::class)
    fun possibleMoves(@Payload field: String, @Header("simpSessionId") sessionId: String) {
        gameHandler.getPossibleMoves(sessionId, field)?.let {
            simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/user/queue/possible-moves",
                it,
                prepareSessionIdHeader(sessionId)
            )
        }
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }
}