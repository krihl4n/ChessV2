package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class RookMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()
        val rook = positionTracker.getPieceAt(from) ?: throw IllegalArgumentException("no piece at $from")

        var nextFile = from.file + 1
        while (nextFile != null) {
            val destination = Field(nextFile, from.rank)
            if (positionTracker.isFieldOccupied(destination)) {
                if (positionTracker.getPieceAt(destination)?.color != rook.color) {
                    possibleMoves.add(PossibleMove(from, destination))
                }
                break
            }
            possibleMoves.add(PossibleMove(from, destination))
            nextFile += 1
        }

        var previousFile = from.file - 1
        while (previousFile != null) {
            val destination = Field(previousFile, from.rank)
            if (positionTracker.isFieldOccupied(destination)) {
                if (positionTracker.getPieceAt(destination)?.color != rook.color) {
                    possibleMoves.add(PossibleMove(from, destination))
                }
                break
            }
            possibleMoves.add(PossibleMove(from, destination))
            previousFile -= 1
        }

        var nextRank = from.rank + 1
        while (nextRank != null) {
            val destination = Field(from.file, nextRank)
            if (positionTracker.isFieldOccupied(destination)) {
                if (positionTracker.getPieceAt(destination)?.color != rook.color) {
                    possibleMoves.add(PossibleMove(from, destination))
                }
                break
            }
            possibleMoves.add(PossibleMove(from, destination))
            nextRank += 1
        }

        var previousRank = from.rank - 1
        while (previousRank != null) {
            val destination = Field(from.file, previousRank)
            if (positionTracker.isFieldOccupied(destination)) {
                if (positionTracker.getPieceAt(destination)?.color != rook.color) {
                    possibleMoves.add(PossibleMove(from, destination))
                }
                break
            }
            possibleMoves.add(PossibleMove(from, destination))
            previousRank -= 1
        }

        return possibleMoves
    }
}
