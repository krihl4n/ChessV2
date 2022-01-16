package com.krihl4n.api

interface GameEventListener {

    fun pieceMoved(gameId: String, from: String, to: String)

    // piece captured
    // pawn replaced by queen
    // undo actions
}