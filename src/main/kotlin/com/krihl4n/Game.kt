package com.krihl4n

import com.krihl4n.model.Field

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

        try {
            positionTracker.movePiece(from, to)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }

        return true
    }
}
