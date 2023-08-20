package com.krihl4n.api

import com.krihl4n.CaptureTracker
import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.api.dto.*
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.game.*
import com.krihl4n.game.Game
import com.krihl4n.game.result.GameResult
import com.krihl4n.game.result.FinishedGameEvaluator
import com.krihl4n.game.result.GameResultObserver
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory
import com.krihl4n.moveCommands.PiecePositionUpdateListener
import com.krihl4n.game.guards.CastlingGuard
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.game.guards.EnPassantGuard
import com.krihl4n.game.positionEvaluators.CheckMateEvaluator
import com.krihl4n.model.Color
import com.krihl4n.model.GameStateUpdate
import com.krihl4n.model.PiecePositionUpdate
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCommands.MoveLabelGenerator
import com.krihl4n.players.Player

class GameOfChess(val gameId: String, val gameMode: String, private val pieceSetup: PieceSetup?) : GameOfChessCommand,
    GameOfChessQuery {

    private val positionTracker = PositionTracker()
    private val commandCoordinator = CommandCoordinator(GameMode.fromCommand(gameMode))
    private val calculatorFactory = CalculatorFactory()
    private val moveCalculator = PieceMoveCalculator(positionTracker, calculatorFactory)
    private val checkEvaluator = CheckEvaluator(positionTracker, moveCalculator)
    private val moveValidator = MoveValidator(moveCalculator, checkEvaluator)
    private val captureTracker = CaptureTracker()
    private val checkMateEvaluator = CheckMateEvaluator(positionTracker, checkEvaluator, moveValidator)
    private val commandFactory = CommandFactory(
        positionTracker,
        MoveLabelGenerator(checkEvaluator, checkMateEvaluator, positionTracker, moveCalculator),
        captureTracker
    )
    private val castlingGuard = CastlingGuard(positionTracker, calculatorFactory)
    private val enPassantGuard = EnPassantGuard(positionTracker, commandCoordinator)
    private val finishedGameEvaluator = FinishedGameEvaluator(positionTracker, moveValidator, checkMateEvaluator)
    private val moveRecorder = MoveRecorder()
    private val game = Game(moveValidator, commandCoordinator, commandFactory, positionTracker, finishedGameEvaluator)

    init {
        calculatorFactory.initCalculators(enPassantGuard, castlingGuard)
        commandCoordinator.registerObserver(this.castlingGuard)
        commandCoordinator.registerObserver(this.finishedGameEvaluator)
        commandCoordinator.registerPiecePositionUpdateListener(this.moveRecorder)
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

    override fun undoMove(playerId: String) = game.undoMove(playerId)

    override fun getMode() = game.getMode().toString()

    override fun getPlayer(playerId: String) = this.game.fetchPlayer(playerId)?.toDto()

    override fun getPlayers() = listOf(this.game.fetchPlayerOne().toDto(), this.game.fetchPlayerTwo().toDto())

    override fun getColorAllowedToMove() = this.game.colorAllowedToMove().toString()

    override fun getFieldOccupationInfo() = game.getFieldOccupationInfo()

    override fun getPossibleMoves(field: String) = game.getPossibleMoves(field)

    override fun getRecordedMoves() = moveRecorder.getMoves()

    override fun getAllCaptures(): CapturesDto {
        return CapturesDto(
            capturesOfWhitePlayer = captureTracker.getCapturedPieces(Color.BLACK).map { PieceDto.from(it) },
            capturesOfBlackPlayer = captureTracker.getCapturedPieces(Color.WHITE).map { PieceDto.from(it) }
        )
    }

    override fun getScore(): ScoreDto {
        val score = captureTracker.getScore()
        return ScoreDto(score.white, score.black)
    }

    override fun getResult(): GameResultDto? {
        return this.game.getResult()?.let {
            GameResultDto(
                result = it.result.toString().lowercase(),
                reason = it.reason.toString().lowercase()
            )
        }
    }

    fun registerGameEventListener(listener: GameEventListener) {
        commandCoordinator.registerPiecePositionUpdateListener(object : PiecePositionUpdateListener {
            override fun positionsUpdated(update: PiecePositionUpdate) {
                listener.piecePositionUpdate(
                    gameId,
                    PiecePositionUpdateDto.from(update, game.fetchColorAllowedToMove(), getScore())
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
                            turn = game.fetchColorAllowedToMove(),
                            recordedMoves = getRecordedMoves(),
                            captures = getAllCaptures(),
                            score = getScore()
                        )
                    )
                }

                if (update.gameState == GameState.WAITING_FOR_PLAYERS) {
                    listener.waitingForOtherPlayer(gameId)
                }
            }
        })

        finishedGameEvaluator.registerObserver(object : GameResultObserver {
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
