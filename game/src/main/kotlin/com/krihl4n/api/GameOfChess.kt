package com.krihl4n.api

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.dto.*
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.game.*
import com.krihl4n.game.Game
import com.krihl4n.game.GameResultEvaluator
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
import java.lang.RuntimeException

class GameOfChess(val gameId: String) {

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

    fun requestNewGame(playerId: String?, mode: GameModeDto, colorPreference: String? = null) {
        println("Request new game: $mode")
        when (mode) {
            GameModeDto.TEST_MODE -> {
                game.initialize(mode)
                game.playerReady("player1", colorPreference)
            }

            GameModeDto.VS_COMPUTER -> { // todo maybe extract and make it a separate module
                game.initialize(mode)
                game.playerReady(playerId ?: "player1", colorPreference)
                game.playerReady("computer")
                val computerColor = game.fetchPlayer("computer")?.color
                    ?: throw RuntimeException("cannot determine opponent color")
                registerGameEventListener(ComputerOpponent(this, "computer", computerColor.toString()))
            }

            GameModeDto.VS_FRIEND -> {
                game.initialize(mode)
                game.playerReady(playerId ?: "player1", colorPreference)
            }
        }
    }

    fun playerTwoReady(playerId: String) {
        game.playerReady(playerId)
    }

    fun resign(playerId: String) {
        game.resign(playerId)
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

        game.registerGameStateUpdateListener(object : GameStateUpdateListener {
            override fun gameStateUpdated(update: GameStateUpdate) {
                listener.gameStateUpdate(
                    gameId, GameStateUpdateDto(
                        gameState = update.gameState
                    )
                )

                if(update.gameState == "IN_PROGRESS") {
                    listener.gameStarted(
                        gameId,
                        GameInfoDto(
                            gameId = gameId,
                            mode = update.gameMode?.toString() ?: "",
                            player1 = PlayerDto.from(game.fetchPlayerOne()),
                            player2 = PlayerDto.from(game.fetchPlayerTwo()),
                            piecePositions = game.getFieldOccupationInfo()
                        )
                    )
                }
            }
        })

        gameResultEvaluator.registerObserver(object : GameResultObserver {
            override fun gameFinished(result: GameResult) {
                listener.gameFinished(
                    gameId, GameResultDto(
                        result = result.result.toString(),
                        reason = result.reason.toString()
                    )
                )
            }
        })
    }

    fun getPossibleMoves(field: String): PossibleMovesDto {
        return game.getPossibleMoves(field)
    }
}