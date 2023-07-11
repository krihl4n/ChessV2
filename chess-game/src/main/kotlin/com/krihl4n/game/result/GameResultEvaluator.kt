package com.krihl4n.game.result

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.game.result.Result.*
import com.krihl4n.game.result.ResultReason.*
import com.krihl4n.game.result.checkers.CheckMateChecker
import com.krihl4n.game.result.checkers.FiftyMoveRepetitionChecker
import com.krihl4n.game.result.checkers.InsufficientMaterialChecker
import com.krihl4n.game.result.checkers.StalemateChecker
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.*
import com.krihl4n.model.Color.*
import com.krihl4n.model.Move
import com.krihl4n.moveCommands.MoveObserver

internal class GameResultEvaluator(
    positionTracker: PositionTracker,
    moveValidator: MoveValidator,
    checkEvaluator: CheckEvaluator
) : MoveObserver {

    private val insufficientMaterial = InsufficientMaterialChecker(positionTracker)
    private val checkMate = CheckMateChecker(positionTracker, checkEvaluator, moveValidator)
    private val fiftyMoveRepetition = FiftyMoveRepetitionChecker()
    private val stalemate = StalemateChecker(positionTracker, moveValidator)

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
            gameFinished(DRAW, REPETITION)
            return
        }
    }

    override fun moveUndid(move: Move) {
        fiftyMoveRepetition.moveUndid()
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
