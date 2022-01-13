package com.krihl4n.api

import com.krihl4n.Game
import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.CheckGuard
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator

class GameOfChess {

    private val positionTracker = PositionTracker()
    private val commandCoordinator = CommandCoordinator()
    private val checkGuard = CheckGuard(positionTracker)
    private val calculatorFactory = CalculatorFactory()
    private val moveValidator = MoveValidator(PieceMoveCalculator(positionTracker, calculatorFactory), checkGuard)
    private val commandFactory = CommandFactory(positionTracker)
    private val castlingGuard = CastlingGuard(positionTracker, calculatorFactory)
    private val enPassantGuard = EnPassantGuard(positionTracker, commandCoordinator)
    private val game = Game(moveValidator, commandCoordinator, commandFactory, positionTracker)

    private var pieceMoveListener: PiecePositionUpdate? = null

    init {
        calculatorFactory.initCalculators(enPassantGuard, castlingGuard)
        commandCoordinator.registerObserver(this.castlingGuard)
    }

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

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return game.getFieldOccupationInfo()
    }

    fun registerMoveListener(listener: PiecePositionUpdate) {
        this.pieceMoveListener = listener
    }
}