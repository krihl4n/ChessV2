package com.krihl4n

import com.krihl4n.api.FieldOccupationDto
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.text.SimpleDateFormat
import java.util.*

@Controller
class GameController (private val gameCoordinator: GameCoordinator){

    //https://www.baeldung.com/spring-websockets-sendtouser
    @MessageMapping("/game")
    @SendTo("/topic/moves")
    @Throws(Exception::class)
    fun move(move: Move): OutputMessage {
        println(move)
        val time = SimpleDateFormat("HH:mm").format(Date())
        return OutputMessage(move.from, move.to, time)
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

    @MessageMapping("/piecePositions")
    @SendTo("/topic/piecePositions")
    @Throws(Exception::class)
    fun piecePositions(command: String): List<FieldOccupationDto> {
        println(command)
        return gameCoordinator.getPositions()
//        return listOf(
//            PiecePosition("a2", Piece("WHITE", "PAWN")),
//            PiecePosition("a7", Piece("BLACK", "PAWN")),
//            PiecePosition("b5", Piece("BLACK", "QUEEN")),
//            PiecePosition("h2", Piece("WHITE", "KING"))
//        )
    }
}