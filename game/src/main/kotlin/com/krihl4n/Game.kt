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

internal class Game(
    private val moveValidator: MoveValidator,
    private val commandCoordinator: CommandCoordinator,
    private val commandFactory: CommandFactory,
    private val positionTracker: PositionTracker
) {

    private var movePolicy: MovePolicy = FreeMovePolicy()

    var gameInProgress = false
    var debugMode = false

    fun setupChessboard(pieceSetup: PieceSetup?) {
        positionTracker.resetInitialGameSetup(pieceSetup)
    }

    @JvmOverloads
    fun start(gameMode: GameMode = GameMode.MOVE_FREELY) {
        gameInProgress = true

        when (gameMode) {
            GameMode.MOVE_FREELY -> this.movePolicy = FreeMovePolicy()
            GameMode.ACTUAL_GAME -> let {
                val policy = ActualGameMovePolicy()
                this.movePolicy = policy
                this.commandCoordinator.registerObserver(policy)
            }
        }

        if (debugMode) {
            println("Start game")
            DebugLogger.printChessboard(positionTracker)
        }
    }

    fun finish() {
        gameInProgress = false
    }

    fun performMove(from: String, to: String): Boolean {
        return this.performMove(Field(from), Field(to))
    }

    fun performMove(from: Field, to: Field): Boolean {
        if (!gameInProgress)
            throw IllegalStateException("Game hasn't been started.")

        if (positionTracker.isFieldEmpty(from)) {
            println("No piece at field $from")
            return false
        }

        val move = positionTracker.getPieceAt(from)?.let { Move(it, from, to) } ?: return false

        if (!movePolicy.moveAllowedBy(move.piece.color)) {
            println("not the ${move.piece.color}'s turn")
            return false
        }

        if (!moveValidator.isMoveValid(move)) {
            println("$move is not valid")
            return false
        }

        try {
            commandCoordinator.execute(commandFactory.getCommand(move))
        } catch (e: IllegalArgumentException) {
            println(e.message)
            return false
        }

        if (debugMode) {
            println(move)
            DebugLogger.printChessboard(positionTracker)
        }
        return true
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

    fun enableDebugMode() {
        this.debugMode = true
    }

    fun disableDebugMode() {
        this.debugMode = false
    }

    fun getPossibleMoves(fieldToken: String): PossibleMovesDto {
        val field = Field(fieldToken)
        this.positionTracker.getPieceAt(field)?.let {
            return PossibleMovesDto.from(field, moveValidator.getValidMoves(field, it.color))
        }
        return PossibleMovesDto.noMovesFrom(field)
    }
}
