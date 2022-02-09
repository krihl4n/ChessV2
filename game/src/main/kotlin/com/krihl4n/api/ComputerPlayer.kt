package com.krihl4n.api

import com.krihl4n.api.dto.PiecePositionUpdateDto
import kotlin.random.Random
import java.util.Timer
import kotlin.concurrent.schedule

class ComputerPlayer(private val game: GameOfChess, private val playerId: String) : GameEventListener {

    override fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto) {

        Timer("OpponentMove", false).schedule(1000) {
            val moveColor = game.getFieldOccupationInfo()
                .first { it.field == update.primaryMove.to }
                .piece?.color

            if (moveColor == "WHITE") {
                val blackPositions = game.getFieldOccupationInfo()
                    .filter { it.piece != null && it.piece.color == "BLACK" }
                    .map { it.field }
                    .toMutableList()

                while (blackPositions.isNotEmpty()) {
                    val randomField = blackPositions[Random.nextInt(0, blackPositions.size)]
                    val possibleMoves = game.getPossibleMoves(randomField)
                    if (possibleMoves.to.isEmpty()) {
                        blackPositions.remove(randomField)
                        continue
                    } else {
                        game.move(playerId, randomField, possibleMoves.to[Random.nextInt(0, possibleMoves.to.size)])
                        break
                    }
                }
            }
        }
    }
}