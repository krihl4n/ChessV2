package com.krihl4n

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.text.SimpleDateFormat
import java.util.*

@Controller
class ChatController {

    //https://www.baeldung.com/spring-websockets-sendtouser
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    @Throws(Exception::class)
    fun send(move: Move): OutputMessage {
        println(move)
        val time = SimpleDateFormat("HH:mm").format(Date())
        return OutputMessage(move.from, move.to, time)
       // return OutputMessage(message, message, time)
    }
}