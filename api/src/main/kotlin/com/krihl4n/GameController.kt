package com.krihl4n

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.text.SimpleDateFormat
import java.util.*

@Controller
class GameController {

    //https://www.baeldung.com/spring-websockets-sendtouser
    @MessageMapping("/game")
    @SendTo("/topic/moves")
    @Throws(Exception::class)
    fun send(move: Move): OutputMessage {
        println(move)
        val time = SimpleDateFormat("HH:mm").format(Date())
        return OutputMessage(move.from, move.to, time)
       // return OutputMessage(message, message, time)
    }

    @MessageMapping("/gameControls")
    @SendTo("/topic/gameControls")
    @Throws(Exception::class)
    fun gameControls(controls: String): OutputMessage {
        println(controls)
        val time = SimpleDateFormat("HH:mm").format(Date())
        return OutputMessage("xxx", "xxx", time)
    }
}