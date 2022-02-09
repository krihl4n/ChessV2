package com.krihl4n.command

import com.krihl4n.model.Move

internal class CommandCoordinator {

    private val executedCommands = ArrayDeque<MoveCommand>()
    private val undidCommands = ArrayDeque<MoveCommand>()

    private val moveObservers = mutableSetOf<MoveObserver>()
    private var piecePositionUpdateListeners = mutableListOf<PiecePositionUpdateListener>()

    fun registerObserver(moveObserver: MoveObserver) {
        this.moveObservers.add(moveObserver)
    }

    fun registerPiecePositionUpdateListener(listener: PiecePositionUpdateListener) {
        this.piecePositionUpdateListeners.add(listener)
    }

    fun execute(command: MoveCommand) {
        command.execute()
        executedCommands.add(command)

        moveObservers.forEach { it.movePerformed(command.getMove()) }
        command.getPiecePositionUpdate()?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it) }}
    }

    fun undo() {
        if (executedCommands.isEmpty())
            return
        val command = executedCommands.last()
        command.undo()
        executedCommands.removeLast()
        undidCommands.add(command)

        moveObservers.forEach { it.moveUndid(command.getMove()) }
        command.getPiecePositionUpdate()?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it) }}
    }

    fun redo() {
        if (undidCommands.isEmpty())
            return
        val command = undidCommands.last()
        command.execute()
        undidCommands.removeLast()
        executedCommands.add(command) // redundant?

        command.getPiecePositionUpdate()?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it) }}
    }

    fun getLastMove(): Move? {
        return if (executedCommands.isNotEmpty()) executedCommands.last().getMove()
        else null
    }
}
