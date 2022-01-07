package com.krihl4n

import com.krihl4n.Dependencies.Companion.commandCoordinator
import com.krihl4n.Dependencies.Companion.commandFactory
import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.api.FieldOccupationDto
import com.krihl4n.api.FieldsOccupationMapper
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.CalculatorFactory

internal class Game(private val moveValidator: MoveValidator) {

    var gameInProgress = false
    var debugMode = false

    fun setupChessboard() {
        positionTracker.resetInitialGameSetup()
    }

    fun start() {
        CalculatorFactory()
        gameInProgress = true
        if (debugMode) {
            println("Start game")
            Dependencies.debugLogger.printChessboard()
        }
    }

    fun finish() {
        gameInProgress = false
    }

    fun performMove(from: String, to: String): Boolean {
        return this.performMove(Field(from), Field(to))
    }

    fun performMove(from: Field, to: Field): Boolean {
        if (!gameInProgress)
            throw IllegalStateException("Game hasn't been started.")

        if(positionTracker.isFieldEmpty(from)) {
            println("No piece at field $from")
            return false
        }

        val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return false

        if (!moveValidator.isMoveValid(move)) {
            println("$move is not valid")
            return false
        }

        try {
            commandCoordinator.execute(commandFactory.getCommand(move))
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }

        if (debugMode) {
            println(move)
            Dependencies.debugLogger.printChessboard()
        }
        return true
    }

    fun undoMove() {
        commandCoordinator.undo()
    }

    fun redoMove() {
        commandCoordinator.redo()
    }

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return FieldsOccupationMapper.from(positionTracker.getPositionsOfAllPieces())
    }

    fun enableDebugMode() {
        this.debugMode = true
    }

    fun disableDebugMode() {
        this.debugMode = false
    }
}
