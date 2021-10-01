package com.krihl4n

class Game(val positionTracker: PositionTracker) {

    var gameInProgress = false

    fun start() {
        gameInProgress = true
    }

    fun finish() {
        gameInProgress = false
    }

    fun performMove(from: Field, to: Field): Boolean {
        if (!gameInProgress)
            throw IllegalStateException("Game hasn't been started.")

        positionTracker.movePiece(from, to)
        return true
    }
}
