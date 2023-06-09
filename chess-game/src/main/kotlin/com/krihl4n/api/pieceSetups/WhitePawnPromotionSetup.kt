package com.krihl4n.api.pieceSetups

class WhitePawnPromotionSetup : PieceSetup {

    override fun get() = listOf(
        "d7 white pawn",
        "e8 black knight",
        "e1 white king",
        "h1 white rook",
        "a8 black king"
    )
}