package com.krihl4n.game

enum class GameState : State {

    UNINITIALIZED {
        override fun start(gameCommand: GameCommand, gameMode: GameMode) {
            gameCommand.setState(WAITING_FOR_PLAYERS)
            gameCommand.executeStart(gameMode)
        }

        override fun forfeit(gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }

        override fun registerPlayer(
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Game not started yet")
        }

        override fun move(gameCommand: GameCommand, playerId: String, from: String, to: String) {
            throw IllegalStateException("Game not started yet")
        }

        override fun undo(gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }

        override fun redo(gameCommand: GameCommand) {
            throw IllegalStateException("Game not started yet")
        }
    },
    WAITING_FOR_PLAYERS {
        override fun start(gameCommand: GameCommand, gameMode: GameMode) {
            throw IllegalStateException("Cannot start, waiting for players")
        }

        override fun forfeit(gameCommand: GameCommand) {
            throw IllegalStateException("Cannot forfeit, waiting for players")
        }

        override fun registerPlayer(
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            val allPlayersRegistered = gameCommand.executeRegisterPlayer(playerId, colorPreference)
            if (allPlayersRegistered) {
                gameCommand.setState(IN_PROGRESS)
            }
        }

        override fun move(gameCommand: GameCommand, playerId: String, from: String, to: String) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun undo(gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun redo(gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move, waiting for players")
        }
    },
    IN_PROGRESS {
        override fun start(gameCommand: GameCommand, gameMode: GameMode) {
            println("Game already started")
        }

        override fun forfeit(gameCommand: GameCommand) {
            gameCommand.setState(FINISHED)
            gameCommand.executeFinish()
        }

        override fun registerPlayer(
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if game in progress")
        }

        override fun move(gameCommand: GameCommand, playerId: String, from: String, to: String) {
            gameCommand.executePerformMove(playerId, from, to)
        }

        override fun undo(gameCommand: GameCommand) {
            gameCommand.executeUndo()
        }

        override fun redo(gameCommand: GameCommand) {
            gameCommand.executeRedo()
        }
    },
    FINISHED {
        override fun start(gameCommand: GameCommand, gameMode: GameMode) {
            gameCommand.setState(IN_PROGRESS)
            gameCommand.executeStart(gameMode)
        }

        override fun forfeit(gameCommand: GameCommand) {
            //do nothing
        }

        override fun registerPlayer(
            gameCommand: GameCommand,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if the game is finished")
        }

        override fun move(gameCommand: GameCommand, playerId: String, from: String, to: String) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun undo(gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun redo(gameCommand: GameCommand) {
            throw IllegalStateException("Cannot move if the game is finished")
        }
    }
}

interface State {

    fun start(gameCommand: GameCommand, gameMode: GameMode)

    fun forfeit(gameCommand: GameCommand)

    fun registerPlayer(gameCommand: GameCommand, playerId: String, colorPreference: String?)

    fun move(gameCommand: GameCommand, playerId: String, from: String, to: String)

    fun undo(gameCommand: GameCommand)

    fun redo(gameCommand: GameCommand)
}