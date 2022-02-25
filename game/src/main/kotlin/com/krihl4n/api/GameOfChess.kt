package com.krihl4n.api

import com.krihl4n.game.Game
import com.krihl4n.game.GameMode
import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.dto.*
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.game.GameResultEvaluator
import com.krihl4n.game.GameStateUpdateListener
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory
import com.krihl4n.moveCommands.PiecePositionUpdateListener
import com.krihl4n.opponent.ComputerOpponent
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.model.GameStateUpdate
import com.krihl4n.model.PiecePositionUpdate
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator

class GameOfChess(private val gameId: String) {

    private val positionTracker = PositionTracker()
    private val commandCoordinator = CommandCoordinator()
    private val calculatorFactory = CalculatorFactory()
    private val moveCalculator = PieceMoveCalculator(positionTracker, calculatorFactory)
    private val checkEvaluator = CheckEvaluator(positionTracker, moveCalculator)
    private val moveValidator = MoveValidator(moveCalculator, checkEvaluator)
    private val commandFactory = CommandFactory(positionTracker)
    private val castlingGuard = CastlingGuard(positionTracker, calculatorFactory)
    private val enPassantGuard = EnPassantGuard(positionTracker, commandCoordinator)
    private val gameResultEvaluator = GameResultEvaluator(positionTracker, moveValidator, checkEvaluator)
    private val game = Game(moveValidator, commandCoordinator, commandFactory, positionTracker, gameResultEvaluator)

    init {
        calculatorFactory.initCalculators(enPassantGuard, castlingGuard)
        commandCoordinator.registerObserver(this.castlingGuard)
        commandCoordinator.registerObserver(this.gameResultEvaluator)
    }

    fun setupChessboard(pieceSetup: PieceSetup? = null) {
        game.setupChessboard(pieceSetup)
    }

    fun start(playerId: String?, mode: GameModeDto) {
        when (mode) {
            GameModeDto.FREE_MOVE -> {
                game.start(GameMode.MOVE_FREELY)
                game.registerPlayer("player1", null)
            }
            GameModeDto.HOT_SEAT -> TODO()
            GameModeDto.VS_COMPUTER -> {
                game.start(GameMode.ACTUAL_GAME)
                game.registerPlayer(playerId ?: "player1", "WHITE")
                game.registerPlayer("player2", "BLACK")
                registerGameEventListener(ComputerOpponent(this, "player2", "BLACK"))
            }
        }
    }

    fun finish() {
        game.finish()
    }

    fun move(playerId: String, from: String, to: String) {
        game.performMove(playerId, from, to)
    }

    fun undoMove() {
        game.undoMove()
    }

    fun redoMove() {
        game.redoMove()
    }

    fun getFieldOccupationInfo(): List<FieldOccupationDto> {
        return game.getFieldOccupationInfo()
    }

    fun registerGameEventListener(listener: GameEventListener) {
        commandCoordinator.registerPiecePositionUpdateListener(object : PiecePositionUpdateListener {
            override fun positionsUpdated(update: PiecePositionUpdate) {
                listener.piecePositionUpdate(gameId, PiecePositionUpdateDto.from(update))
            }
        })

        game.registerGameStateUpdateListener(object: GameStateUpdateListener {
            override fun gameStateUpdated(update: GameStateUpdate) {
                listener.gameStateUpdate(gameId, GameStateUpdateDto(
                    gameState = update.gameState,
                    stateChangeReason = update.updateReason?.toString()
                ))
            }
        })
    }

    fun getPossibleMoves(field: String): PossibleMovesDto {
        return game.getPossibleMoves(field)
    }
}