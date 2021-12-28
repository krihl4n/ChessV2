package com.krihl4n

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
}

private data class Capture(val piece: Piece, val field: Field)
