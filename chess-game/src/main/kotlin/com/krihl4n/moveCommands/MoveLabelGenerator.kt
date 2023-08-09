package com.krihl4n.moveCommands

import com.krihl4n.PositionTracker
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.model.File
import com.krihl4n.model.Move
import com.krihl4n.model.Type
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class MoveLabelGenerator(private val checkEvaluator: CheckEvaluator, private val positionTracker: PositionTracker, private val moveCalculator: PieceMoveCalculator) {

    fun getLabel(move: Move): String {
        val piece = pieceTypeLabel(move.piece.type)
        val departure = getFieldOfDepartureIfNeeded(move)
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
        return piece + departure + attack + destination + pawnPromotion + check
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

    private fun getFieldOfDepartureIfNeeded(performedMove: Move): String {
        val moves = findOtherMovesWithSameDstAs(performedMove)
        var departure = ""

        if(moves.anyMoveHasSameFromRankAs(performedMove)) {
            departure += performedMove.from.file.token
        }

        if(moves.anyMoveHasSameFromFileAs(performedMove)) {
            departure += performedMove.from.rank.token
        }
        return departure
    }

    private fun findOtherMovesWithSameDstAs(move: Move): List<PossibleMove> {
        val positionTracker = this.positionTracker.copy()
        positionTracker.movePiece(move.to, move.from)
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == move.piece.color && it.value.type == move.piece.type }
            .flatMap { this.moveCalculator.withPositionTracker(positionTracker).findMoves(it.key) }
            .filter { it.to == move.to && it.from != move.from }
    }

    private fun List<PossibleMove>.anyMoveHasSameFromRankAs(move: Move)  =
        this.any {it.from.rank == move.from.rank}

    private fun List<PossibleMove>.anyMoveHasSameFromFileAs(move: Move)  =
        this.any {it.from.file == move.from.file}
}
