package com.krihl4n

import com.krihl4n.moveCalculators.PieceMoveCalculator

class GameOfChess {

    private val dependencies = Dependencies()
    private val moveValidator = MoveValidator(PieceMoveCalculator(Dependencies.positionTracker))
    private val game = Game(moveValidator)


    fun start() {
        game.start()
    }

    fun finish() {
        game.finish()
    }

    fun move(from: String, to: String) {
        game.performMove(from, to)
    }

    fun undoMove() {
        game.undoMove()
    }

    fun redoMove() {
        game.redoMove()
    }

    fun getPositions() {

    }
}