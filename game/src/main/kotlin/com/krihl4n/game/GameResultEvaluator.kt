package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.*
import com.krihl4n.model.Move
import com.krihl4n.moveCommands.MoveObserver

internal class GameResultEvaluator(
    val positionTracker: PositionTracker,
    private val moveValidator: MoveValidator,
    private val checkEvaluator: CheckEvaluator
) :
    MoveObserver {

    private val resultObservers = mutableListOf<GameResultObserver>()
    private var result: GameResult? = null

    override fun movePerformed(move: Move) {
        if (isKingChecked(move.piece.color.opposite())) {
            if (!isThereASavingMove(move.piece.color.opposite())) {
                println("check-mate!")
                this.result = move.piece.color.let {
                    if (it == Color.WHITE) {
                        GameResult(Result.WHITES_WON, ResultReason.CHECK_MATE)
                    } else {
                        GameResult(Result.BLACKS_WON, ResultReason.CHECK_MATE)
                    }
                }
                notifyGameFinished()
            }
        } else if (noMoreValidMovesFor(move.piece.color.opposite())) {
            this.result = GameResult(Result.DRAW, ResultReason.STALEMATE)
            notifyGameFinished()
        } else if (insufficientMaterial()) {
            this.result = GameResult(Result.DRAW, ResultReason.DEAD_POSITION)
            notifyGameFinished()
        }
    }

    private fun insufficientMaterial(): Boolean {
        val piecesLeft = positionTracker.getPositionsOfAllPieces().map { it.value }

        if (piecesLeft.size == 2 && piecesLeft[0].type == Type.KING && piecesLeft[1].type == Type.KING)
            return true

        // todo remaining cases
        return false
    }

    private fun noMoreValidMovesFor(color: Color): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .map { moveValidator.getValidMoves(it.key, color) }
            .all { it.isEmpty() }
    }

    override fun moveUndid(move: Move) {
        // todo
    }

    fun getGameResult(): GameResult? = result

    fun isGameFinished(): Boolean {  // maybe not needed
        return false
    }

    private fun isThereASavingMove(color: Color): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { moveValidator.getValidMoves(it.key, color) }
            .firstOrNull {
                !checkEvaluator.isKingCheckedAfterMove(
                    color = color,
                    possibleMove = it
                )
            } != null
    }

    fun registerObserver(observer: GameResultObserver) {
        resultObservers.add(observer)
    }

    private fun notifyGameFinished() {
        resultObservers.forEach { it.gameFinished() }
    }

    private fun isKingChecked(color: Color): Boolean {
        return checkEvaluator.isKingChecked(color)
    }
}
