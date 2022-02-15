package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory

internal class Game(
    moveValidator: MoveValidator,
    commandCoordinator: CommandCoordinator,
    commandFactory: CommandFactory,
    positionTracker: PositionTracker
) : StateHolder {

    private var gameState: State = GameState.UNINITIALIZED

    override fun setState(state: State) {
        this.gameState = state
    }

    private val gameControl: GameControl = GameControl(
        moveValidator,
        commandCoordinator,
        commandFactory,
        positionTracker
    )

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
}
