package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.pieceSetups.PieceSetup
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

    private val gameStateListeners = mutableListOf<GameStateUpdateListener>()

    override fun setState(state: State) {
        this.gameState = state
        gameStateListeners.forEach {it.gameStateUpdated(GameStateUpdate(state.toString()))}
    }

    private val gameControl: GameControl = GameControl(
        moveValidator,
        commandCoordinator,
        commandFactory,
        positionTracker
    )

    init {
        gameResultEvaluator.registerObserver(this)
    }

    fun isGameFinished() = gameState == GameState.FINISHED

    fun getResult() = gameResultEvaluator.getGameResult()

    fun setupChessboard(pieceSetup: PieceSetup?) {
        gameControl.setupChessboard(pieceSetup)
    }

    @JvmOverloads
    fun start(gameMode: GameMode = GameMode.MOVE_FREELY) = gameState.start(this, gameControl, gameMode)

    fun registerPlayer(playerId: String, colorPreference: String?) =
        gameState.registerPlayer(this, gameControl, playerId, colorPreference)

    fun finish() = gameState.forfeit(this, gameControl)

    fun performMove(playerId: String, from: String, to: String) =
        gameState.move(this, gameControl, playerId, from, to)

    fun undoMove() = gameState.undo(this, gameControl)

    fun redoMove() = gameState.redo(this, gameControl)

    fun getFieldOccupationInfo() = gameControl.getFieldOccupationInfo()

    fun getPossibleMoves(fieldToken: String) = gameControl.getPossibleMoves(fieldToken)

    override fun gameFinished(result: GameResult) {
        gameState.gameFinished(this)
    }

    fun registerGameStateUpdateListener(gameStateUpdateListener: GameStateUpdateListener) {
        gameStateListeners.add(gameStateUpdateListener)
    }
}
