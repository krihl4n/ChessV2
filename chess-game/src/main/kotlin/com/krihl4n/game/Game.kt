package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.model.Color
import com.krihl4n.model.GameStateUpdate
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory

internal class Game(
    moveValidator: MoveValidator,
    commandCoordinator: CommandCoordinator,
    commandFactory: CommandFactory,
    positionTracker: PositionTracker,
    private val gameResultEvaluator: GameResultEvaluator
) : StateHolder, GameResultObserver {

    private var gameState: State = GameState.UNINITIALIZED
    private var gameMode: GameMode? = null
    private val gameStateListeners = mutableListOf<GameStateUpdateListener>()
    private val gameControl: GameControl = GameControl(
        moveValidator,
        commandCoordinator,
        commandFactory,
        positionTracker,
        gameResultEvaluator
    )

    init {
        gameResultEvaluator.registerObserver(this)
    }

    override fun setState(state: State) {
        this.gameState = state
        gameStateListeners.forEach {
            it.gameStateUpdated(
                GameStateUpdate(
                    gameState = state,
                    gameMode = this.gameMode ?: throw RuntimeException("No game mode!")
                )
            )
        }
    }

    fun initialize(gameMode: GameMode) {
        this.gameMode = gameMode
        gameState.initializeGame(this, gameControl, gameMode)
    }

    fun setupChessboard(pieceSetup: PieceSetup?) {
        gameControl.setupChessboard(pieceSetup)
    }

    fun playerReady(playerId: String, colorPreference: String? = null) =
        gameState.playerReady(this, gameControl, playerId, colorPreference, this.gameMode)

    fun resign(playerId: String) = gameState.resign(this, gameControl, playerId)

    fun performMove(move: MoveDto) =
        gameState.move(this, gameControl, move)

    fun undoMove(playerId: String) = gameState.undo(this, gameControl, playerId)

    fun isGameFinished() = gameState == GameState.FINISHED // todo remove, used only in tests

    fun getResult() = gameResultEvaluator.getGameResult()

    fun getMode() = this.gameMode

    fun colorAllowedToMove(): Color = this.gameControl.fetchColorAllowedToMove()

    fun fetchPlayer(playerId: String) = gameControl.fetchPlayer(playerId)

    fun fetchPlayerOne() = gameControl.fetchPlayerOne()

    fun fetchPlayerTwo() = gameControl.fetchPlayerTwo()

    fun fetchColorAllowedToMove() = gameControl.fetchColorAllowedToMove().toString()

    fun getFieldOccupationInfo() = gameControl.getFieldOccupationInfo()

    fun getPossibleMoves(fieldToken: String) = gameControl.getPossibleMoves(fieldToken)

    override fun gameFinished(result: GameResult) {
        gameState.gameFinished(this)
    }

    fun registerGameStateUpdateListener(gameStateUpdateListener: GameStateUpdateListener) {
        gameStateListeners.add(gameStateUpdateListener)
    }
}
