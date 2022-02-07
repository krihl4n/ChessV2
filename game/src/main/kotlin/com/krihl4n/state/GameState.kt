package com.krihl4n.state

import com.krihl4n.GameMode

enum class GameState : State {

    UNINITIALIZED {
        override fun start(gameControllable: GameControllable, gameMode: GameMode) {
            gameControllable.setState(WAITING_FOR_PLAYERS)
            gameControllable.executeStart(gameMode)
        }

        override fun forfeit(gameControllable: GameControllable) {
            throw IllegalStateException("Game not started yet")
        }

        override fun registerPlayer(
            gameControllable: GameControllable,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Game not started yet")
        }

        override fun move(gameControllable: GameControllable, from: String, to: String) {
            throw IllegalStateException("Game not started yet")
        }

        override fun undo(gameControllable: GameControllable) {
            throw IllegalStateException("Game not started yet")
        }

        override fun redo(gameControllable: GameControllable) {
            throw IllegalStateException("Game not started yet")
        }
    },
    WAITING_FOR_PLAYERS {
        override fun start(gameControllable: GameControllable, gameMode: GameMode) {
            throw IllegalStateException("Cannot start, waiting for players")
        }

        override fun forfeit(gameControllable: GameControllable) {
            throw IllegalStateException("Cannot forfeit, waiting for players")
        }

        override fun registerPlayer(
            gameControllable: GameControllable,
            playerId: String,
            colorPreference: String?
        ) {
            val allPlayersRegistered = gameControllable.executeRegisterPlayer(playerId, colorPreference)
            if (allPlayersRegistered) {
                gameControllable.setState(IN_PROGRESS)
            }
        }

        override fun move(gameControllable: GameControllable, from: String, to: String) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun undo(gameControllable: GameControllable) {
            throw IllegalStateException("Cannot move, waiting for players")
        }

        override fun redo(gameControllable: GameControllable) {
            throw IllegalStateException("Cannot move, waiting for players")
        }
    },
    IN_PROGRESS {
        override fun start(gameControllable: GameControllable, gameMode: GameMode) {
            println("Game already started")
        }

        override fun forfeit(gameControllable: GameControllable) {
            gameControllable.setState(FINISHED)
            gameControllable.executeFinish()
        }

        override fun registerPlayer(
            gameControllable: GameControllable,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if game in progress")
        }

        override fun move(gameControllable: GameControllable, from: String, to: String) {
            gameControllable.executePerformMove(from, to)
        }

        override fun undo(gameControllable: GameControllable) {
            gameControllable.executeUndo()
        }

        override fun redo(gameControllable: GameControllable) {
            gameControllable.executeRedo()
        }
    },
    FINISHED {
        override fun start(gameControllable: GameControllable, gameMode: GameMode) {
            gameControllable.setState(IN_PROGRESS)
            gameControllable.executeStart(gameMode)
        }

        override fun forfeit(gameControllable: GameControllable) {
            //do nothing
        }

        override fun registerPlayer(
            gameControllable: GameControllable,
            playerId: String,
            colorPreference: String?
        ) {
            throw IllegalStateException("Cannot register if the game is finished")
        }

        override fun move(gameControllable: GameControllable, from: String, to: String) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun undo(gameControllable: GameControllable) {
            throw IllegalStateException("Cannot move if the game is finished")
        }

        override fun redo(gameControllable: GameControllable) {
            throw IllegalStateException("Cannot move if the game is finished")
        }
    }
}

interface State {

    fun start(gameControllable: GameControllable, gameMode: GameMode)

    fun forfeit(gameControllable: GameControllable)

    fun registerPlayer(gameControllable: GameControllable, playerId: String, colorPreference: String?)

    fun move(gameControllable: GameControllable, from: String, to: String)

    fun undo(gameControllable: GameControllable)

    fun redo(gameControllable: GameControllable)
}