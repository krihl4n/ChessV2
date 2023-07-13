package com.krihl4n.game.positionEvaluators

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import com.krihl4n.moveCalculators.PossibleMove

internal class ThreefoldRepEvaluator(
    private val positionTracker: PositionTracker,
    private val moveValidator: MoveValidator
) {
    private val positions = ArrayDeque<Position>()

    fun occurs(): Boolean {
        return positions.insertAndCount(calculatePosition())
    }

    fun moveUndid() {
        positions.removeLast()
    }

    private fun calculatePosition(): Position {
        val allPositions = positionTracker.getPositionsOfAllPieces()
        val possibleMoves: List<PossibleMove> =
            allPositions.keys.flatMap { moveValidator.getValidMoves(it, allPositions[it]!!.color) }
        return Position(allPositions, possibleMoves)
    }

    private fun ArrayDeque<Position>.insertAndCount(position:Position): Boolean {
        this.add(position)
        return this.count { it == position } == 3
    }
}

private data class Position(private val piecePositions: HashMap<Field, Piece>, val possibleMoves: List<PossibleMove>)
