package com.krihl4n

import com.krihl4n.api.dto.FieldOccupationDto
import com.krihl4n.api.dto.PossibleMovesDto
import com.krihl4n.api.mappers.FieldsOccupationMapper
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.command.CommandFactory
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.turns.ActualGameMovePolicy
import com.krihl4n.turns.FreeMovePolicy
import com.krihl4n.turns.MovePolicy

internal class GameControl(
    private val moveValidator: MoveValidator,
    private val commandCoordinator: CommandCoordinator,
    private val commandFactory: CommandFactory,
    private val positionTracker: PositionTracker) {


    private var movePolicy: MovePolicy = FreeMovePolicy()

    fun setupChessboard(pieceSetup: PieceSetup?) {
        positionTracker.resetInitialGameSetup(pieceSetup)
    }

    @JvmOverloads
    fun start(gameMode: GameMode = GameMode.MOVE_FREELY) {
        when (gameMode) {
            GameMode.MOVE_FREELY -> this.movePolicy = FreeMovePolicy()
            GameMode.ACTUAL_GAME -> let {
                val policy = ActualGameMovePolicy()
                this.movePolicy = policy
                this.commandCoordinator.registerObserver(policy)
            }
        }

        DebugLogger.printChessboard(positionTracker)
    }

    fun finish() {
        // todo
    }

    fun performMove(from: String, to: String) {
        this.performMove(Field(from), Field(to))
    }

    fun performMove(from: Field, to: Field) {
        if (positionTracker.isFieldEmpty(from)) {
            println("No piece at field $from")
            return
        }

        val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return

        if (!movePolicy.moveAllowedBy(move.piece.color)) {
            println("not the ${move.piece.color}'s turn")
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
}
