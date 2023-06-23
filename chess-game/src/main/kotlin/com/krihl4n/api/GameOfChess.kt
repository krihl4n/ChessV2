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
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.model.GameStateUpdate
import com.krihl4n.model.PiecePositionUpdate
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.players.Player

class GameOfChess(val gameId: String, val gameMode: String, private val pieceSetup: PieceSetup?): GameOfChessCommand {

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

    override fun initialize() {
        game.setupChessboard(pieceSetup)
        game.initialize(GameMode.fromCommand(gameMode))
    }

    override fun playerReady(playerId: String, colorPreference: String?) {
        game.playerReady(playerId, colorPreference)
    }

    override fun resign(playerId: String) = game.resign(playerId)

    override fun move(move: MoveDto) = game.performMove(move)

    override fun undoMove() = game.undoMove()

    override fun redoMove() = game.redoMove()

    fun getMode() = game.getMode().toString()

    fun getPlayer(playerId: String) = this.game.fetchPlayer(playerId)?.toDto()

    fun getPlayers() = listOf(this.game.fetchPlayerOne().toDto(), this.game.fetchPlayerTwo().toDto())

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
                        gameState = update.gameState.toString()
                    )
                )

                if (update.gameState == GameState.IN_PROGRESS) {
                    listener.gameStarted(
                        gameId,
                        GameInfoDto(
                            gameId = gameId,
                            mode = update.gameMode.toString(),
                            player1 = game.fetchPlayerOne().toDto(),
                            player2 = game.fetchPlayerTwo().toDto(),
                            piecePositions = game.getFieldOccupationInfo(),
                            turn = game.fetchColorAllowedToMove()
                        )
                    )
                }

                if(update.gameState == GameState.WAITING_FOR_PLAYERS) {
                    listener.waitingForOtherPlayer(gameId)
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

    private fun Player.toDto() = PlayerDto(this.id, this.color.toString())
}
