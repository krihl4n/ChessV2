package com.krihl4n.api.pieceSetups

class SimpleAttackSetup : PieceSetup {

    override fun get() = listOf(
        "c2 white pawn",
        "d3 black pawn",
        "a1 white king",
        "h1 black king"
    )
}