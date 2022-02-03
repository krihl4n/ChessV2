package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.state.GameControllable
import com.krihl4n.state.GameState
import com.krihl4n.state.State

internal class Game(
    moveValidator: MoveValidator,
    commandCoordinator: CommandCoordinator,
    commandFactory: CommandFactory,
    positionTracker: PositionTracker
) : GameControllable {

    var gameState: State = GameState.UNINITIALIZED

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
    fun start(gameMode: GameMode = GameMode.MOVE_FREELY) {
        gameState.start(this, gameMode)
    }

    override fun executeStart(gameMode: GameMode) {
        gameControl.start(gameMode)
    }

    override fun executeFinish() {
        gameControl.finish()
    }

    override fun executePerformMove(from: String, to: String) {
        gameControl.performMove(from, to)
    }

    override fun executeUndo() {
        gameControl.undoMove()
    }

    override fun executeRedo() {
        gameControl.redoMove()
    }

    fun finish() {
        gameState.forfeit(this)
    }

    fun performMove(from: String, to: String) {
        gameState.move(this, from, to)
    }

    fun undoMove() {
        gameControl.undoMove()
    }

    fun redoMove() {
        gameControl.redoMove()
    }

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return gameControl.getFieldOccupationInfo()
    }

    fun getPossibleMoves(fieldToken: String): PossibleMovesDto {
        return gameControl.getPossibleMoves(fieldToken)
    }
}
