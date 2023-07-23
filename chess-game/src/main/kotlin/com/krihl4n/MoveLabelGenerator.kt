package com.krihl4n

import com.krihl4n.model.File
import com.krihl4n.model.Move
import com.krihl4n.model.Type

internal object MoveLabelGenerator {

    fun getLabel(move: Move): String {
        val piece = when (move.piece.type) {
            Type.PAWN -> ""
            Type.KNIGHT -> "N"
            Type.BISHOP -> "B"
            Type.ROOK -> "R"
            Type.QUEEN -> "Q"
            Type.KING -> "K"
        }
        val attack = if (move.isAttack && move.piece.type == Type.PAWN) {
            move.from.file.token.lowercase() + "x"
        } else if (move.isAttack) {
            "x"
        } else {
            ""
        }
        val destination = move.to.token().lowercase()
        val pawnPromotion = move.pawnPromotion?.let { "Q" } ?: ""
        return piece + attack + destination + pawnPromotion
    }

    fun getLabelForCastling(move: Move): String {
        return when (move.to.file) {
            File("g") -> "O-O"
            File("c") -> "O-O-O"
            else -> throw RuntimeException("Not castling")
        }
    }
}