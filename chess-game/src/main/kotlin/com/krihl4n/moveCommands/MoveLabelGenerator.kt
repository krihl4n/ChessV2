package com.krihl4n.moveCommands

import com.krihl4n.PositionTracker
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.model.File
import com.krihl4n.model.Move
import com.krihl4n.model.Type
import com.krihl4n.moveCalculators.PieceMoveCalculator

internal class MoveLabelGenerator(private val checkEvaluator: CheckEvaluator, private val positionTracker: PositionTracker, private val moveCalculator: PieceMoveCalculator) {

    fun getLabel(move: Move): String {
        val piece = pieceTypeLabel(move.piece.type)
        val fileOfDeparture = getFileOfDepartureIfNeeded(move)
        val rankOfDeparture = getRankOfDepartureIfNeeded(move)
        val attack = if (move.isAttack && move.piece.type == Type.PAWN) {
            move.from.file.token.lowercase() + "x"
        } else if (move.isAttack) {
            "x"
        } else {
            ""
        }
        val destination = move.to.token().lowercase()
        val pawnPromotion = move.pawnPromotion?.let { pieceTypeLabel(it) } ?: ""
        val check = if (checkEvaluator.isKingChecked(move.piece.color.opposite())) "+" else ""
        return piece + fileOfDeparture + rankOfDeparture + attack + destination + pawnPromotion + check
    }

    fun getLabelForCastling(move: Move): String {
        return when (move.to.file) {
            File("g") -> "O-O"
            File("c") -> "O-O-O"
            else -> throw RuntimeException("Not castling")
        }
    }

    private fun pieceTypeLabel(type: Type) =
        when (type) {
            Type.PAWN -> ""
            Type.KNIGHT -> "N"
            Type.BISHOP -> "B"
            Type.ROOK -> "R"
            Type.QUEEN -> "Q"
            Type.KING -> "K"
        }

    private fun getRankOfDepartureIfNeeded(performedMove: Move): String {
        val positionTracker = this.positionTracker.copy()
        positionTracker.movePiece(performedMove.to, performedMove.from)

        val rankMatches = positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == performedMove.piece.color && it.value.type == performedMove.piece.type }
            .flatMap { this.moveCalculator.withPositionTracker(positionTracker).findMoves(it.key) }
            .filter { it.to == performedMove.to && it.from != performedMove.from }
            .any { it.from.file == performedMove.from.file }

        return if (rankMatches) {
            performedMove.from.rank.token
        } else {
            ""
        }
    }

    private fun getFileOfDepartureIfNeeded(performedMove: Move): String {
        val positionTracker = this.positionTracker.copy()
        positionTracker.movePiece(performedMove.to, performedMove.from)

        val fileMatches = positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == performedMove.piece.color && it.value.type == performedMove.piece.type }
            .flatMap { this.moveCalculator.withPositionTracker(positionTracker).findMoves(it.key) }
            .filter { it.to == performedMove.to && it.from != performedMove.from }
            .any { it.from.rank == performedMove.from.rank }

        return if (fileMatches) {
            performedMove.from.file.token
        } else {
            ""
        }
    }
}
