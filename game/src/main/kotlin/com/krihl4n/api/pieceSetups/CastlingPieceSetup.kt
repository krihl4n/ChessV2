package com.krihl4n.api.pieceSetups

class CastlingPieceSetup : PieceSetup {

    override fun get() = listOf(
        "e1 white king",
        "h1 white rook",
        "a8 black king"
    )
}