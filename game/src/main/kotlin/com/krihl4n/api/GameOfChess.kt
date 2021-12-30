package com.krihl4n.api

import com.krihl4n.Dependencies
import com.krihl4n.Game
import com.krihl4n.MoveValidator
import com.krihl4n.moveCalculators.PieceMoveCalculator

class GameOfChess {

    private val dependencies = Dependencies()
    private val moveValidator = MoveValidator(PieceMoveCalculator(Dependencies.positionTracker))
    private val game = Game(moveValidator)

    private var pieceMoveListener: PiecePositionUpdate? = null

    fun setupChessboard() {
        game.setupChessboard()
    }

    fun start() {
        game.enableDebugMode()
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

    fun registerMoveListener(listener: PiecePositionUpdate) {
        this.pieceMoveListener = listener
    }
}