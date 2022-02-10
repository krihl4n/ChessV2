package com.krihl4n.api

import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.dto.PossibleMovesDto
import kotlin.random.Random
import java.util.Timer
import kotlin.concurrent.schedule

class ComputerPlayer(private val game: GameOfChess, private val playerId: String, private val playerColor: String) :
    GameEventListener {

    override fun piecePositionUpdate(sessionId: String, update: PiecePositionUpdateDto) {

        if (determineColorOfPieceThatMoved(update) == playerColor) {
            return
        }

        Timer("ScheduleMove", false).schedule(1000) {
            performRandomMove()
        }
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

    private fun getPositionsOfPiecesOfColor(playerColor: String) = game.getFieldOccupationInfo()
        .filter { it.piece != null && it.piece.color == playerColor }
        .map { it.field }
        .toMutableList()
}
