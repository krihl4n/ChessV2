package com.krihl4n.model

internal class PiecePositionUpdate(
    val primaryMove: Move,
    val secondaryMove: Move? = null,
    val pieceCapture: PieceCapture? = null,
    var reverted: Boolean = false
) {

    fun reverted(): PiecePositionUpdate {
        return PiecePositionUpdate(primaryMove, secondaryMove, pieceCapture, true)
    }
}

internal data class PieceCapture(
    val field: Field,
    val piece: Piece
)
