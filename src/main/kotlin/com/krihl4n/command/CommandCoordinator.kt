package com.krihl4n.command

class CommandCoordinator {

    private val executedCommands = ArrayDeque<MoveCommand>()
    private val undidCommands = ArrayDeque<MoveCommand>()

    fun execute(command: MoveCommand) {
        command.execute()
        executedCommands.add(command)
    }

    fun undo() {
        if (executedCommands.isEmpty())
            return
        val command = executedCommands.last()
        command.undo()
        executedCommands.removeLast()
        undidCommands.add(command)
    }

    fun redo() {
        if (undidCommands.isEmpty())
            return
        val command = undidCommands.last()
        command.execute()
        undidCommands.removeLast()
        executedCommands.add(command)
    }
}
