package com.krihl4n.opponent

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.game.GameState
import kotlin.random.Random
import java.util.Timer
import kotlin.concurrent.schedule

class ComputerOpponent(private val game: GameOfChess, private val playerId: String, private val playerColor: String) :
    GameEventListener {

    init {
        if(playerColor.lowercase() == "white") {
            Timer("ScheduleMove", false).schedule(1000) {
                performRandomMove()
            }
        }
    }
    override fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto) {

        if (determineColorOfPieceThatMoved(update) == playerColor) {
            return
        }

        Timer("ScheduleMove", false).schedule(1000) {
            val attacked = attackIfPossible()
            if(!attacked) {
                performRandomMove()
            }
        }
    }

    override fun gameStateUpdate(sessionId: String, update: GameStateUpdateDto) {
        println(update)
    }

    override fun gameFinished(sessionId: String, result: GameResultDto) {
        // do nothing?
    }

    private fun attackIfPossible(): Boolean{
        val positions = getPositionsOfPiecesOfColor(playerColor)
        val opponentPositions = getPositionsOfPiecesOfColor(oppositeColorOf(playerColor))
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                for(dst in possibleMoves.to) {
                    if(opponentPositions.contains(dst)) {
                        game.move(playerId, field, dst)
                        return true
                    }
                }
                positions.remove(field)
            }
        }
        return false
    }

    private fun oppositeColorOf(color: String): String {
        return if(color.lowercase() == "white") "black" else "white"
    }

    private fun performRandomMove() {
        val positions = getPositionsOfPiecesOfColor(playerColor)
        while (positions.isNotEmpty()) {
            val field = getRandomField(positions)
            val possibleMoves = getPossibleMovesFrom(field)
            if (noMovesAvailable(possibleMoves)) {
                positions.remove(field)
                continue
            } else {
                game.move(playerId, field, possibleMoves.to[Random.nextInt(0, possibleMoves.to.size)])
                break
            }
        }
    }

    private fun noMovesAvailable(possibleMoves: PossibleMovesDto) = possibleMoves.to.isEmpty()

    private fun getPossibleMovesFrom(randomField: String) = game.getPossibleMoves(randomField)

    private fun getRandomField(playersPiecePositions: MutableList<String>) =
        playersPiecePositions[Random.nextInt(0, playersPiecePositions.size)]

    private fun determineColorOfPieceThatMoved(update: PiecePositionUpdateDto) =
        game.getFieldOccupationInfo()
            .first { it.field == update.primaryMove.to }
            .piece?.color

    private fun getPositionsOfPiecesOfColor(playerColor: String): MutableList<String> = game.getFieldOccupationInfo()
        .filter { it.piece != null && it.piece.color.lowercase() == playerColor.lowercase() }
        .map { it.field }
        .toMutableList()
}
