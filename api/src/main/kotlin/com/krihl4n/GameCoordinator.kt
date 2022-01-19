package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.GameOfChess
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameCoordinator {

    private var gameOfChess = GameOfChess("")
    private var player1Id: String? = null
    private var player2Id: String? = null

    private var player1:Player? = null // for now player one is always white
    private var player2:Player? = null

    fun registerPlayer(player: Player) {

        if(player1 == null) {
            player1 = player
            player1Id = UUID.randomUUID().toString()
            player.idAssigned(player1Id!!)
            return
        }

        if(player2 == null) {
            player2 = player
            player2Id = UUID.randomUUID().toString()
            player.idAssigned(player2Id!!)
            return
        }

        throw IllegalStateException("All players already registered")
    }


    fun reassignPlayer(id: String, player: Player) {
        // todo
    }

    fun startGame() {
        gameOfChess.setupChessboard()
        gameOfChess.start()
    }

    fun move(playerId: String, from: String, to: String) {
//        if(playerId != player1Id && playerId != player2Id) {
//            throw IllegalArgumentException("Player $playerId is not registered in this game")
//        }

        gameOfChess.move(from, to)
    }

    fun getPositions(): List<FieldOccupationDto> {
        return gameOfChess.getFieldOccupationInfo()
    }
}

/*
    state machine?
    started?
    waiting for players
    in progress
    finished
 */