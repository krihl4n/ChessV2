package com.krihl4n.moveCommands

import com.krihl4n.model.Move

internal class CommandCoordinator {

    private val executedCommands = ArrayDeque<MoveCommand>()

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

        moveObservers.forEach { it.moveUndid(command.getMove()) }
        command.getPiecePositionUpdate()?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it.reverted()) }}
    }

    fun getLastMove(): Move? {
        return if (executedCommands.isNotEmpty()) executedCommands.last().getMove()
        else null
    }
}
