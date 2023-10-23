package com.krihl4n.app

import com.krihl4n.GameCommandHandler
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.app.messages.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
        logger.debug("--> /move | sessionId={} | {}", sessionId, move)
        gameCommandHandler.move(
            move.gameId, MoveDto(
                playerId = move.playerId,
                from = move.from,
                to = move.to,
                pawnPromotion = move.pawnPromotion
            )
        )
    }

    @MessageMapping("/start-new-game") // request-new-game
    @Throws(Exception::class)
    fun startNewGame(@Payload request: StartGameRequest, @Header("simpSessionId") sessionId: String) {
        logger.info("--> /start-new-game | sessionId={} | {}", sessionId, request)
        gameCommandHandler.requestNewGame(sessionId, request) // maybe return ack?
    }

    @MessageMapping("/join-game")
    @Throws(Exception::class)
    fun joinGame(@Payload request: JoinGameRequest, @Header("simpSessionId") sessionId: String) {
        logger.info("--> /join-game | sessionId={} | {}", sessionId, request)
        gameCommandHandler.joinGame(sessionId, request)
    }

    @MessageMapping("/rejoin-game")
    @Throws(Exception::class)
    fun joinGame(@Payload request: RejoinGameRequest, @Header("simpSessionId") sessionId: String) {
        logger.info("--> /rejoin-game | sessionId={} | {}", sessionId, request)
        gameCommandHandler.rejoinGame(sessionId, request)
    }

    @MessageMapping("/rematch")
    @Throws(Exception::class)
    fun rematch(
        @Payload gameId: String,
        @Header("simpSessionId") sessionId: String
    ) { // todo object of some sort? with gameId maybe?
        logger.info("--> /rematch | sessionId=$sessionId | gameId=$gameId")
        gameCommandHandler.requestRematch(sessionId, gameId)
    }

    @MessageMapping("/possible-moves")
    @Throws(Exception::class)
    fun possibleMoves(@Payload req: PossibleMovesRequest, @Header("simpSessionId") sessionId: String) {
        gameCommandHandler.getPossibleMoves(req.gameId, req.field)?.let {
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
    fun resign(@Payload req: ResignRequest, @Header("simpSessionId") sessionId: String) {
        logger.info("--> /resign | sessionId=$sessionId | playerId=${req.playerId}")
        gameCommandHandler.resign(req.gameId, req.playerId)
    }

    @MessageMapping("/undo-move")
    @Throws(Exception::class)
    fun undoMove(@Payload req: UndoMoveRequest, @Header("simpSessionId") sessionId: String) {
        logger.debug("--> /undo-move | sessionId=$sessionId | playerId=${req.playerId}")
        gameCommandHandler.undoMove(req.gameId, req.playerId)
    }

    private fun prepareSessionIdHeader(sessionId: String): MessageHeaders {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        return headerAccessor.messageHeaders
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(GameController::class.java)
    }
}