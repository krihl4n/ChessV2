package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import java.util.*

internal class CaptureTracker {

    private val capturedPieces = LinkedList<Capture>()

    fun pieceCaptured(piece: Piece, field: Field) {
        capturedPieces.addLast(Capture(piece, field))
    }

    fun popLastPieceCapturedAtField(field: Field): Piece {
        val capture = capturedPieces.last { it.field == field }
        capturedPieces.removeLastOccurrence(capture)
        return capture.piece
    }

    fun getCapturedPieces(piecesColor: Color) =
        capturedPieces.filter { it.piece.color == piecesColor }.map { it.piece }

    fun getScore(): Score {
        var white = 0
        var black = 0

        capturedPieces.forEach { capture ->
            if (capture.piece.color == Color.BLACK) {
                white += capture.piece.type.value
            } else {
                black += capture.piece.type.value
            }
        }

        return if (white > black) {
            Score(white - black, 0)
        } else if (white < black) {
            Score(0, black - white)
        } else {
            Score(0, 0)
        }
    }
}

data class Score(val white: Int, val black: Int)

private data class Capture(val piece: Piece, val field: Field)
