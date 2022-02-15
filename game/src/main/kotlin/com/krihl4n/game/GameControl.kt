package com.krihl4n.game

import com.krihl4n.DebugLogger
import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.api.mappers.FieldsOccupationMapper
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.players.ActualGamePlayersManager
import com.krihl4n.players.FreeMovePlayersManager
import com.krihl4n.players.PlayersManager
import com.krihl4n.turns.ActualGameMovePolicy
import com.krihl4n.turns.FreeMovePolicy
import com.krihl4n.turns.MovePolicy

internal class GameControl(
    private val moveValidator: MoveValidator,
    private val commandCoordinator: CommandCoordinator,
    private val commandFactory: CommandFactory,
    private val positionTracker: PositionTracker
) : GameCommand {

    private var movePolicy: MovePolicy = FreeMovePolicy()
    private var playersManager: PlayersManager = FreeMovePlayersManager()

    fun setupChessboard(pieceSetup: PieceSetup?) {
        positionTracker.resetInitialGameSetup(pieceSetup)
    }

    @JvmOverloads
    fun start(gameMode: GameMode = GameMode.MOVE_FREELY) {
        when (gameMode) {
            GameMode.MOVE_FREELY -> {
                this.movePolicy = FreeMovePolicy()
                this.playersManager = FreeMovePlayersManager()
            }
            GameMode.ACTUAL_GAME -> let {
                this.playersManager = ActualGamePlayersManager()
                val policy = ActualGameMovePolicy(playersManager)
                this.movePolicy = policy
                this.commandCoordinator.registerObserver(policy)
            }
        }

        DebugLogger.printChessboard(positionTracker)
    }

    fun registerPlayer(playerId: String, colorPreference: String?): Boolean {
        return this.playersManager.registerPlayer(playerId, colorPreference?.let { Color.of(it) })
    }

    fun finish() {
        // todo
    }

    fun performMove(playerId: String, from: String, to: String) {
        this.performMove(playerId, Field(from), Field(to))
    }

    fun performMove(playerId: String, from: Field, to: Field) {
        if (positionTracker.isFieldEmpty(from)) {
            println("No piece at field $from")
            return
        }

        val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return

        if (!movePolicy.moveAllowedBy(playerId)) {
            println("not the ${move.piece.color}'s turn. player id: $playerId")
            return
        }

        if (!moveValidator.isMoveValid(move)) {
            println("$move is not valid")
            return
        }

        try {
            commandCoordinator.execute(commandFactory.getCommand(move))
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return
        }

        DebugLogger.printChessboard(positionTracker)
        return
    }

    fun undoMove() {
        commandCoordinator.undo()
    }

    fun redoMove() {
        commandCoordinator.redo()
    }

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return FieldsOccupationMapper.from(positionTracker.getPositionsOfAllPieces())
    }

    fun getPossibleMoves(fieldToken: String): PossibleMovesDto {
        val field = Field(fieldToken)
        this.positionTracker.getPieceAt(field)?.let {
            return PossibleMovesDto.from(field, moveValidator.getValidMoves(field, it.color))
        }
        return PossibleMovesDto.noMovesFrom(field)
    }

    override fun executeStart(gameMode: GameMode) {
        start(gameMode)
    }

    override fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean {
        return registerPlayer(playerId, colorPreference)
    }

    override fun executeFinish() {
        finish()
    }

    override fun executePerformMove(playerId: String, from: String, to: String) {
        performMove(playerId, from, to)
    }

    override fun executeUndo() {
        undoMove()
    }

    override fun executeRedo() {
        redoMove()
    }
}
