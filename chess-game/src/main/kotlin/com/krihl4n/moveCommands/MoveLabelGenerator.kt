package com.krihl4n.moveCommands

import com.krihl4n.PositionTracker
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.game.positionEvaluators.CheckMateEvaluator
import com.krihl4n.model.File
import com.krihl4n.model.Move
import com.krihl4n.model.Type
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

// todo result 1-0
internal class MoveLabelGenerator(
    private val checkEvaluator: CheckEvaluator,
    private val checkMateEvaluator: CheckMateEvaluator,
    private val positionTracker: PositionTracker,
    private val moveCalculator: PieceMoveCalculator
) {

    fun getLabel(move: Move): String {
        val piece = pieceTypeLabel(move.piece.type)
        val departure = getFieldOfDepartureIfNeeded(move)
        val attack =
            if (move.isAttack && move.piece.type == Type.PAWN) move.from.file.token.lowercase() + "x" else if (move.isAttack) "x" else ""
        val destination = move.to.token().lowercase()
        val pawnPromotion = move.pawnPromotion?.let { pieceTypeLabel(it) } ?: ""
        val check = if (isCheckMate(move)) "#" else if (isCheck(move)) "+" else ""
        return piece + departure + attack + destination + pawnPromotion + check
    }

    private fun isCheckMate(move: Move) = checkMateEvaluator.occurs(move.piece.color.opposite())

    private fun isCheck(move: Move) = checkEvaluator.isKingChecked(move.piece.color.opposite())

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
        if (performedMove.piece.type == Type.PAWN) {
            return ""
        }
        val moves = findOtherMovesWithSameDstAs(performedMove)
        val fileToken = performedMove.from.file.token
        val rankToken = performedMove.from.rank.token
        var departure = ""

        if (moves.size == 1) {
            if (moves[0].hasDifferentFromFileThan(performedMove)) {
                departure = fileToken
            } else if (moves[0].hasDifferentFromRankThan(performedMove)) {
                departure = rankToken
            }
        } else if (moves.size > 1) {
            if (moves.containsMoveWithSameFromFileAs(performedMove) && moves.containsMoveWithSameFromRankAs(
                    performedMove
                )
            ) {
                departure = fileToken + rankToken
            } else {
                if (moves.containsMoveWithSameFromRankAs(performedMove)) {
                    departure = fileToken
                }
                if (moves.containsMoveWithSameFromFileAs(performedMove)) {
                    departure = rankToken
                }
            }
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

    private fun PossibleMove.hasDifferentFromFileThan(move: Move) = this.from.file != move.from.file

    private fun PossibleMove.hasDifferentFromRankThan(move: Move) = this.from.rank != move.from.rank

    private fun List<PossibleMove>.containsMoveWithSameFromFileAs(move: Move) =
        find { it.from.file == move.from.file } != null

    private fun List<PossibleMove>.containsMoveWithSameFromRankAs(move: Move) =
        find { it.from.rank == move.from.rank } != null
}
