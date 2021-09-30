package com.krihl4n

class Game {

    var gameInProgress = false

    fun start() {
        gameInProgress = true
    }

    fun finish() {
        gameInProgress = false
    }

    fun move(piece: Piece, from: Field, to: Field): Boolean {
        if (!gameInProgress)
            throw IllegalStateException("Game hasn't been started.")
        return true
    }
}
