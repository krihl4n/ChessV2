package com.krihl4n.game.result

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.game.positionEvaluators.*
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.game.positionEvaluators.CheckMateEvaluator
import com.krihl4n.game.positionEvaluators.FiftyMoveRepetitionEvaluator
import com.krihl4n.game.positionEvaluators.InsufficientMaterialEvaluator
import com.krihl4n.game.positionEvaluators.StalemateEvaluator
import com.krihl4n.game.result.Result.*
import com.krihl4n.game.result.ResultReason.*
import com.krihl4n.model.*
import com.krihl4n.model.Color.*
import com.krihl4n.model.Move
import com.krihl4n.moveCommands.MoveObserver

internal class FinishedGameEvaluator(
    positionTracker: PositionTracker,
    moveValidator: MoveValidator,
    checkEvaluator: CheckEvaluator
) : MoveObserver {

    private val insufficientMaterial = InsufficientMaterialEvaluator(positionTracker)
    private val checkMate = CheckMateEvaluator(positionTracker, checkEvaluator, moveValidator)
    private val fiftyMoveRepetition = FiftyMoveRepetitionEvaluator()
    private val stalemate = StalemateEvaluator(positionTracker, moveValidator)
    private val threefoldRepetition = ThreefoldRepEvaluator(positionTracker, moveValidator)

    private val resultObservers = mutableListOf<GameResultObserver>()
    private var result: GameResult? = null

    override fun movePerformed(move: Move) {
        if (checkMate.occurs(move.piece.color.opposite())) {
            if (move.piece.color == WHITE) {
                gameFinished(WHITE_PLAYER_WON, CHECK_MATE)
            } else {
                gameFinished(BLACK_PLAYER_WON, CHECK_MATE)
            }
            return
        }

        if (stalemate.occurs(move.piece.color.opposite())) {
            gameFinished(DRAW, STALEMATE)
            return
        }
        if (insufficientMaterial.occurs()) {
            gameFinished(DRAW, INSUFFICIENT_MATERIAL)
            return
        }
        if (fiftyMoveRepetition.occurs(move)) {
            gameFinished(DRAW, FIFTY_MOVE_REPETITION)
            return
        }
        if (threefoldRepetition.occurs()) {
            gameFinished(DRAW, THREEFOLD_REPETITION)
            return
        }
    }

    override fun moveUndid(move: Move) {
        fiftyMoveRepetition.moveUndid()
        threefoldRepetition.moveUndid()
    }

    fun getGameResult(): GameResult? = result

    fun resign(resigningPlayerColor: Color) {
        when (resigningPlayerColor) {
            WHITE -> this.result = GameResult(BLACK_PLAYER_WON, PLAYER_RESIGNED)
            BLACK -> this.result = GameResult(WHITE_PLAYER_WON, PLAYER_RESIGNED)
        }
        notifyGameFinished()
    }

    fun registerObserver(observer: GameResultObserver) {
        resultObservers.add(observer)
    }

    private fun gameFinished(result: Result, reason: ResultReason) {
        this.result = GameResult(result, reason)
        notifyGameFinished()
    }

    private fun notifyGameFinished() {
        println("Game finished: ${this.result}")
        this.result?.let { result -> resultObservers.forEach { it.gameFinished(result) } }
    }
}
