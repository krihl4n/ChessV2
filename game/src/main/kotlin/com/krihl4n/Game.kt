package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory
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

    fun registerPlayer(playerId: String, colorPreference: String?) {
        gameState.registerPlayer(this, playerId, colorPreference)
    }

    override fun executeStart(gameMode: GameMode) {
        gameControl.start(gameMode)
    }

    override fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean {
        return gameControl.registerPlayer(playerId, colorPreference)
    }

    override fun executeFinish() {
        gameControl.finish()
    }

    override fun executePerformMove(playerId: String, from: String, to: String) {
        gameControl.performMove(playerId, from, to)
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

    fun performMove(playerId: String, from: String, to: String) {
        gameState.move(this, playerId, from, to)
    }

    fun undoMove() {
        gameState.undo(this)
    }

    fun redoMove() {
        gameState.redo(this)
    }

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return gameControl.getFieldOccupationInfo()
    }

    fun getPossibleMoves(fieldToken: String): PossibleMovesDto {
        return gameControl.getPossibleMoves(fieldToken)
    }
}
