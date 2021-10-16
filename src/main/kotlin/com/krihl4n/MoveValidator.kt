package com.krihl4n
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.PieceMoveCalculator

class MoveValidator(private val pieceMoveCalculator: PieceMoveCalculator) {

    fun isMoveValid(move: Move) : Boolean {
        val possibleMoves = pieceMoveCalculator.findMoves(move.from)
        return possibleMoves.stream().anyMatch { it.from == move.from && it.to == move.to }
    }
}
