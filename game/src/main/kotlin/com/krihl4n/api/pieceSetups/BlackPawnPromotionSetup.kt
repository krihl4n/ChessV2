package com.krihl4n.api.pieceSetups

class BlackPawnPromotionSetup : PieceSetup {

    override fun get() = listOf(
        "d2 black pawn",
        "e1 white knight",
        "e8 black king",
        "h8 black rook",
        "a1 white king"
    )
}