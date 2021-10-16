package com.krihl4n

import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.model.Field
import com.krihl4n.model.Move

class Game(
    private val positionTracker: PositionTracker,
    private val commandCoordinator: CommandCoordinator,
    private val commandFactory: CommandFactory
) {

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
            val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return false
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
