package com.krihl4n

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.text.SimpleDateFormat
import java.util.*

@Controller
class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    @Throws(Exception::class)
    fun send(message: Message): OutputMessage {
        println("received message")
        val time = SimpleDateFormat("HH:mm").format(Date())
        return OutputMessage(message.text ?: "default", message.from ?: "default", time)
       // return OutputMessage(message, message, time)
    }
}