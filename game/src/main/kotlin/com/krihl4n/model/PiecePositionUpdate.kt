package com.krihl4n.model

internal data class PiecePositionUpdate(
    val primaryMove: Move,
    val secondaryMove: Move? = null,
    val pieceCapture: PieceCapture? = null
)

internal data class PieceCapture(
    val field: Field,
    val piece: Piece
)
