package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

internal class DebugLogger {

    fun printChessboard() {
        val piecePositions = getPositionsFromTracker(Dependencies.positionTracker.getPositionsOfAllPieces())

        var chessboard = ""

        chessboard += "    ---- ---- ---- ---- ---- ---- ---- ----\n"

        //for ( i = 8; i > 0; i--) {
        for( i in 8 downTo 1 step 1) {
            chessboard +=
                i.toString() + "  | " + getPiece("a" + i, piecePositions) + " |" +
                        " " + getPiece("b" + i, piecePositions) + " |" +
                        " " + getPiece("c" + i, piecePositions) + " |" +
                        " " + getPiece("d" + i, piecePositions) + " |" +
                        " " + getPiece("e" + i, piecePositions) + " |" +
                        " " + getPiece("f" + i, piecePositions) + " |" +
                        " " + getPiece("g" + i, piecePositions) + " |" +
                        " " + getPiece("h" + i, piecePositions) + " |" +
                        "\n"
            chessboard += "    ---- ---- ---- ---- ---- ---- ---- ----\n"
        }

        chessboard += "     a    b    c    d    e    f    g    h"
        println(chessboard)
    }

    private fun getPiece(field: String, piecePositions: Map<String, String>): String {
        val piece = piecePositions[field]
        if (piece == null) return "  " else return piece
    }

    private fun getPositionsFromTracker(mapFromTracker: Map<Field, Piece> ):  Map<String, String> {
        val map = mutableMapOf<String, String>()
        mapFromTracker.forEach { k, v ->
            val stringVal = k.toString()
            val stripped = stringVal.substring(stringVal.indexOf('(') + 1, stringVal.indexOf(')'))
            map.put(stripped, determineToken(v.color) + determineToken(v.type))
        }
        return map
    }

    private fun determineToken(pieceType: Type): String {
        return when (pieceType) {
            Type.PAWN -> "p"
            Type.ROOK -> return "r"
            Type.KNIGHT -> return "n"
            Type.BISHOP -> return "b"
            Type.QUEEN -> return "q"
            Type.KING -> return "k"
        }
    }

    private fun determineToken(pieceColor: Color): String {
        return when (pieceColor) {
            Color.WHITE -> "w"
            Color.BLACK -> "b"
        }
    }
}