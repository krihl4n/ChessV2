package com.krihl4n.api.pieceSetups

class AboutToCheckMateSetup : PieceSetup {

    override fun get() = listOf(
        "a1 black king",
        "g2 white rook",
        "h2 white rook"
    )
}