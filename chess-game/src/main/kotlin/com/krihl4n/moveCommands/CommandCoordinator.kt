package com.krihl4n.moveCommands

import com.krihl4n.game.GameMode
import com.krihl4n.model.Move
import com.krihl4n.players.Player

internal class CommandCoordinator(private val gameMode: GameMode) {

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
        command.getPiecePositionUpdate()?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it) } }
    }

    fun undo(player: Player) {
        if (executedCommands.isEmpty())
            return
        when (gameMode) {
            GameMode.TEST_MODE -> undoLastMove()
            GameMode.VS_COMPUTER -> {
                if (!lastMoveBy(player) && secondToLastMoveBy(player)) {
                    undoLastMove()
                    undoLastMove()
                } else if (lastMoveBy(player)) {
                    undoLastMove()
                }
            }

            GameMode.VS_FRIEND -> {
                if (lastMoveBy(player)) {
                    undoLastMove()
                }
            }
        }
    }

    fun getLastMove(): Move? {
        return if (executedCommands.isNotEmpty()) executedCommands.last().getMove()
        else null
    }

    private fun lastMoveBy(player: Player) = executedCommands.last().getMove().piece.color == player.color

    private fun secondToLastMoveBy(player: Player) =
        executedCommands.size >= 2 && executedCommands[executedCommands.size - 2].getMove().piece.color == player.color

    private fun undoLastMove(): Boolean {
        val command = executedCommands.last()
        command.undo()
        executedCommands.removeLast()

        moveObservers.forEach { it.moveUndid(command.getMove()) }
        command.getPiecePositionUpdate()
            ?.let { piecePositionUpdateListeners.forEach { l -> l.positionsUpdated(it.reverted()) } }
        return false
    }
}
