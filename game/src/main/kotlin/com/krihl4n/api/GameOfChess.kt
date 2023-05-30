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
import java.util.UUID

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

    fun setupChessboard(pieceSetup: PieceSetup? = null) = game.setupChessboard(pieceSetup)

    fun requestNewGame(mode: GameModeDto) {
        game.initialize(mode)
    }

    fun playerReady(playerId: String, colorPreference: String?) {
        game.playerReady(playerId, colorPreference)
        game.getMode()?.let {
            if (it == GameModeDto.VS_COMPUTER) {
                game.playerReady(UUID.randomUUID().toString())
                val computerPlayer = game.fetchPlayerTwo()
                registerGameEventListener(ComputerOpponent(this, computerPlayer.id, computerPlayer.color.toString()))
            }
        }
    }

    fun resign(playerId: String) = game.resign(playerId)

    fun move(move: MoveDto) = game.performMove(move)

    fun undoMove() = game.undoMove()

    fun redoMove() = game.redoMove()

    fun getMode() = game.getMode()

    fun getPlayer(playerId: String) = this.game.fetchPlayer(playerId)

    fun getPlayers() = listOf(this.game.fetchPlayerOne(), this.game.fetchPlayerTwo())

    fun getColorAllowedToMove() = this.game.colorAllowedToMove().toString()

    fun getFieldOccupationInfo() = game.getFieldOccupationInfo()

    fun getPossibleMoves(field: String) = game.getPossibleMoves(field)

    fun registerGameEventListener(listener: GameEventListener) {
        commandCoordinator.registerPiecePositionUpdateListener(object : PiecePositionUpdateListener {
            override fun positionsUpdated(update: PiecePositionUpdate) {
                listener.piecePositionUpdate(
                    gameId,
                    PiecePositionUpdateDto.from(update, game.fetchColorAllowedToMove())
                )
            }
        })

        game.registerGameStateUpdateListener(object : GameStateUpdateListener {
            override fun gameStateUpdated(update: GameStateUpdate) {
                listener.gameStateUpdate(
                    gameId, GameStateUpdateDto(
                        gameState = update.gameState
                    )
                )

                if (update.gameState == "IN_PROGRESS") {
                    listener.gameStarted(
                        gameId,
                        GameInfoDto(
                            gameId = gameId,
                            mode = update.gameMode?.toString() ?: "",
                            player1 = PlayerDto.from(game.fetchPlayerOne()),
                            player2 = PlayerDto.from(game.fetchPlayerTwo()),
                            piecePositions = game.getFieldOccupationInfo(),
                            turn = game.fetchColorAllowedToMove()
                        )
                    )
                }
            }
        })

        gameResultEvaluator.registerObserver(object : GameResultObserver {
            override fun gameFinished(result: GameResult) {
                listener.gameFinished(
                    gameId, GameResultDto(
                        result = result.result.toString().lowercase(),
                        reason = result.reason.toString().lowercase()
                    )
                )
            }
        })
    }
}