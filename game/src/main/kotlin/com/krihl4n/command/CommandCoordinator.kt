package com.krihl4n.command

import com.krihl4n.model.Move

internal class CommandCoordinator() {

    private val executedCommands = ArrayDeque<MoveCommand>()
    private val undidCommands = ArrayDeque<MoveCommand>()

    private val moveObservers = mutableSetOf<MoveObserver>()
    private var piecePositionUpdateListener: PiecePositionUpdateListener? = null

    fun registerObserver(moveObserver: MoveObserver) {
        this.moveObservers.add(moveObserver)
    }

    fun registerPiecePositionUpdateListener(listener: PiecePositionUpdateListener) {
        this.piecePositionUpdateListener = listener
    }

    fun execute(command: MoveCommand) {
        command.execute()
        executedCommands.add(command)

        moveObservers.forEach { it.movePerformed(command.getMove()) }

        command.getPiecePositionUpdate()?.let { piecePositionUpdateListener?.positionsUpdated(it) }
//
//        piecePositionUpdateListener?.pieceMoved( // todo send more complex object instead of two moves
//            gameId,
//            command.getPiecePositionUpdate()!!.primaryMove.from,
//            command.getPiecePositionUpdate()!!.primaryMove.to
//        )
//
//        if(command.getPiecePositionUpdate()!!.secondaryMove != null) {
//            piecePositionUpdateListener?.pieceMoved(
//                gameId,
//                command.getPiecePositionUpdate()!!.secondaryMove!!.from,
//                command.getPiecePositionUpdate()!!.secondaryMove!!.to
//            )
//        }
    }

    fun undo() {
        if (executedCommands.isEmpty())
            return
        val command = executedCommands.last()
        command.undo()
        executedCommands.removeLast()
        undidCommands.add(command)

        moveObservers.forEach { it.moveUndid(command.getMove()) }
    }

    fun redo() {
        if (undidCommands.isEmpty())
            return
        val command = undidCommands.last()
        command.execute()
        undidCommands.removeLast()
        executedCommands.add(command)
    }

    fun getLastMove(): Move? {
        return if (executedCommands.isNotEmpty()) executedCommands.last().getMove()
        else null
    }
}
