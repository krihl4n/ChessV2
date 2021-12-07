package com.krihl4n

import com.krihl4n.Dependencies.Companion.commandCoordinator
import com.krihl4n.Dependencies.Companion.commandFactory
import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.Move

class Game(
    private val moveValidator: MoveValidator
) {

    var gameInProgress = false

    fun start() {
        gameInProgress = true
    }

    fun finish() {
        gameInProgress = false
    }

    fun performMove(from: String, to:String): Boolean {
        return this.performMove(Field(from), Field(to))
    }

    fun performMove(from: Field, to: Field): Boolean {
        if (!gameInProgress)
            throw IllegalStateException("Game hasn't been started.")

        val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return false
        if (!moveValidator.isMoveValid(move)){
            println("$move is not valid")
            return false
        }

        try {
            commandCoordinator.execute(commandFactory.getCommand(move))
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }

        return true
    }

    fun undoMove() {
        commandCoordinator.undo()
    }

    fun redoMove() {
        commandCoordinator.redo()
    }

}
