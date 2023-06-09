package com.krihl4n.api.pieceSetups

class ReproduceBugSetup : PieceSetup {

    // setup for bug_08_12_2022.txt
    // ugly fix applied
    // move c8 b7 causes stack overflow
    override fun get() = listOf(
        "c8 black bishop",
        "e8 black king",
        "e1 white king",
    )
}