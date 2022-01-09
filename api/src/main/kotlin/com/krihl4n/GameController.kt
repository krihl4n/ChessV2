package com.krihl4n

import com.krihl4n.api.FieldOccupationDto
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.SimpMessageType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.text.SimpleDateFormat
import java.util.*


@Controller
class GameController (private val gameCoordinator: GameCoordinator,  private val simpMessagingTemplate: SimpMessagingTemplate){

    // https://medium.com/swlh/websockets-with-spring-part-3-stomp-over-websocket-3dab4a21f397 todo subscribe, error handling
    // https://www.baeldung.com/spring-websockets-sendtouser
    @MessageMapping("/move")
    @SendTo("/topic/moves")
    @Throws(Exception::class)
    fun move(move: Move, @Header("simpSessionId") sessionId: String): Move {
        println(move)
        gameCoordinator.move("", move.from, move.to)
        return Move(move.from, move.to) // todo return only if really performed.
    }

    @MessageMapping("/gameControls")
    @SendTo("/topic/gameControls")
    @Throws(Exception::class)
    fun gameControls(controls: String): OutputMessage {
        println(controls)
        val time = SimpleDateFormat("HH:mm").format(Date())
        gameCoordinator.startGame()
        return OutputMessage("xxx", "xxx", time)
    }

    @MessageMapping("/fieldsOccupation")
    @SendTo("/topic/fieldsOccupation")
    @Throws(Exception::class)
    fun fieldsOccupation(command: String): List<FieldOccupationDto> {
        println(command)
        return gameCoordinator.getPositions()
    }

    @MessageMapping("/secured/chat")
    fun sendSpecific(@Payload msg: String, @Header("simpSessionId") sessionId: String) {
        val headerAccessor = SimpMessageHeaderAccessor
            .create(SimpMessageType.MESSAGE)
        headerAccessor.sessionId = sessionId
        //todo later on it will have to be send to specific user who can have multiple sessions
        simpMessagingTemplate.convertAndSendToUser(sessionId,"/secured/user/queue/specific-user", "response", headerAccessor.messageHeaders)
    }
}