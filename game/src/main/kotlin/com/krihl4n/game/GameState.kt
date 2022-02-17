package com.krihl4n.game

enum class GameState : State {

    UNINITIALIZED {
        override fun start(stateHolder: StateHolder, gameCommand: GameCommand, gameMode: GameMode) {
            stateHolder.setState(WAITING_FOR_PLAYERS)
            gameCommand.executeStart(gameMode)
        }

        override fun forfeit(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }

        override fun registerPlayer(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Game not started yet")
        }

        override fun move(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            from: String,
            to: String
        ) {
            throw IllegalStateException("Game not started yet")
        }

        override fun undo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }

        override fun redo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }

        override fun gameFinished(stateHolder: StateHolder) {
            stateHolder.setState(FINISHED)
        }
    },
    WAITING_FOR_PLAYERS {
        override fun start(stateHolder: StateHolder, gameCommand: GameCommand, gameMode: GameMode) {
            throw IllegalStateException("Cannot start, waiting for players")
        }

        override fun forfeit(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Cannot forfeit, waiting for players")
        }

        override fun registerPlayer(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            val allPlayersRegistered = gameCommand.executeRegisterPlayer(playerId, colorPreference)
            if (allPlayersRegistered) {
                stateHolder.setState(IN_PROGRESS)
            }
        }

        override fun move(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            from: String,
            to: String
        ) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun undo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun redo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun gameFinished(stateHolder: StateHolder) {
            stateHolder.setState(FINISHED)
        }
    },
    IN_PROGRESS {
        override fun start(stateHolder: StateHolder, gameCommand: GameCommand, gameMode: GameMode) {
            println("Game already started")
        }

        override fun forfeit(stateHolder: StateHolder, gameCommand: GameCommand) {
            stateHolder.setState(FINISHED)
            gameCommand.executeFinish()
        }

        override fun registerPlayer(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if game in progress")
        }

        override fun move(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            from: String,
            to: String
        ) {
            gameCommand.executePerformMove(playerId, from, to)
        }

        override fun undo(stateHolder: StateHolder, gameCommand: GameCommand) {
            gameCommand.executeUndo()
        }

        override fun redo(stateHolder: StateHolder, gameCommand: GameCommand) {
            gameCommand.executeRedo()
        }

        override fun gameFinished(stateHolder: StateHolder) {
            stateHolder.setState(FINISHED)
        }
    },
    FINISHED {
        override fun start(stateHolder: StateHolder, gameCommand: GameCommand, gameMode: GameMode) {
            stateHolder.setState(IN_PROGRESS)
            gameCommand.executeStart(gameMode)
        }

        override fun forfeit(stateHolder: StateHolder, gameCommand: GameCommand) {
            //do nothing
        }

        override fun registerPlayer(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if the game is finished")
        }

        override fun move(
            stateHolder: StateHolder,
            gameCommand: GameCommand,
            playerId: String,
            from: String,
            to: String
        ) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun undo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun redo(stateHolder: StateHolder, gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun gameFinished(stateHolder: StateHolder) {
            // do nothing?
        }
    }
}

interface State {

    fun start(stateHolder: StateHolder, gameCommand: GameCommand, gameMode: GameMode)
    fun forfeit(stateHolder: StateHolder, gameCommand: GameCommand)
    fun registerPlayer(stateHolder: StateHolder, gameCommand: GameCommand, playerId: String, colorPreference: String?)
    fun move(stateHolder: StateHolder, gameCommand: GameCommand, playerId: String, from: String, to: String)
    fun undo(stateHolder: StateHolder, gameCommand: GameCommand)
    fun redo(stateHolder: StateHolder, gameCommand: GameCommand)
    fun gameFinished(stateHolder: StateHolder)
}