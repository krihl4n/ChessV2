package com.krihl4n.app

import com.krihl4n.GameCommandHandler
import com.krihl4n.requests.JoinGameRequest
import com.krihl4n.requests.Move
import com.krihl4n.requests.StartGameRequest
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
    private val gameCommandHandler: GameCommandHandler,
    private val simpMessagingTemplate: SimpMessagingTemplate,
) {

    // https://medium.com/swlh/websockets-with-spring-part-3-stomp-over-websocket-3dab4a21f397 todo subscribe, error handling
    // https://www.baeldung.com/spring-websockets-sendtouser

    @MessageMapping("/move")
    @Throws(Exception::class)
    fun move(@Payload move: Move, @Header("simpSessionId") sessionId: String) {
        println("--> /move | sessionId=$sessionId | $move")
        gameCommandHandler.move(sessionId, move.playerId, move.from, move.to)
    }

    @MessageMapping("/start-new-game") // request-new-game
    @Throws(Exception::class)
    fun startNewGame(@Payload request: StartGameRequest, @Header("simpSessionId") sessionId: String) {
        println("--> /start-new-game | sessionId=$sessionId | $request")
        gameCommandHandler.requestNewGame(sessionId, request) // maybe return ack?
    }

    @MessageMapping("/join-game")
    @Throws(Exception::class)
    fun joinGame(@Payload request: JoinGameRequest, @Header("simpSessionId") sessionId: String) {
        println("--> /join-game | sessionId=$sessionId | $request")
        gameCommandHandler.joinGame(sessionId, request)
    }

    @MessageMapping("/fields-occupation")
    @Throws(Exception::class)
    fun fieldsOccupation(@Payload command: String, @Header("simpSessionId") sessionId: String) {
        gameCommandHandler.getPositions(sessionId)?.let {
            simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/fields-occupation",
                it,
                prepareSessionIdHeader(sessionId)
            )
        }
    }

    @MessageMapping("/possible-moves")
    @Throws(Exception::class)
    fun possibleMoves(@Payload field: String, @Header("simpSessionId") sessionId: String) {
        gameCommandHandler.getPossibleMoves(sessionId, field)?.let {
            simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/possible-moves",
                it,
                prepareSessionIdHeader(sessionId)
            )
        }
    }

    @MessageMapping("/resign")
    @Throws(Exception::class)
    fun resign(@Payload playerId: String, @Header("simpSessionId") sessionId: String) {
        println("--> /resign | sessionId=$sessionId | playerId=$playerId")
        gameCommandHandler.resign(sessionId, playerId)
    }

    @MessageMapping("/undo-move")
    @Throws(Exception::class)
    fun undoMove(@Payload playerId: String, @Header("simpSessionId") sessionId: String) {
        println("--> /undo-move | sessionId=$sessionId | playerId=$playerId")
        gameCommandHandler.undoMove(sessionId, playerId)
    }

    @MessageMapping("/redo-move")
    @Throws(Exception::class)
    fun redoMove(@Payload playerId: String, @Header("simpSessionId") sessionId: String) {
        println("--> /resign | sessionId=$sessionId | playerId=$playerId")
        gameCommandHandler.redoMove(sessionId, playerId)
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }
}