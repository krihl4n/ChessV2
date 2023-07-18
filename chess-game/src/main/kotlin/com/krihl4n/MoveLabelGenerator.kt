package com.krihl4n

import com.krihl4n.model.Move
import com.krihl4n.model.Type

internal object MoveLabelGenerator {

    fun getLabel(move: Move): String {
        val pieceAbbreviation = when(move.piece.type) {
            Type.PAWN -> ""
            Type.KNIGHT -> "N"
            Type.BISHOP -> "B"
            Type.ROOK -> "R"
            Type.QUEEN -> "Q"
            Type.KING -> "K"
        }
        return pieceAbbreviation + move.to.token().lowercase()
    }
}