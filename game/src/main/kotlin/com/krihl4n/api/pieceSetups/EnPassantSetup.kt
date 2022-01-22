package com.krihl4n.api.pieceSetups

class EnPassantSetup : PieceSetup {

    override fun get() = listOf(
        "d2 white pawn",
        "e4 black pawn"
    )
}