package com.krihl4n.game

import com.krihl4n.PositionTracker
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.*
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove
import com.krihl4n.moveCommands.MoveObserver

internal class GameResultEvaluator(
    val positionTracker: PositionTracker,
    val moveCalculator: PieceMoveCalculator,
    private val checkEvaluator: CheckEvaluator
) :
    MoveObserver {

    private val resultObservers = mutableListOf<GameResultObserver>()
    private var result: GameResult? = null

    override fun movePerformed(move: Move) {
        if (isKingChecked(move.piece.color.opposite())) {
            if(!isThereASavingMove(move.piece.color.opposite())){
                println("check-mate!")
                this.result = move.piece.color.let {
                    if(it == Color.WHITE){
                        GameResult(Result.WHITES_WON, ResultReason.CHECK_MATE)
                    } else {
                        GameResult(Result.BLACKS_WON, ResultReason.CHECK_MATE)
                    }
                }
                notifyGameFinished()
            }
        }
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
            .flatMap { moveCalculator.findMoves(it.key) }
            .firstOrNull {
                !checkEvaluator.isKingCheckedAfterMove(
                    moveCalculator,
                    color,
                    PossibleMove(it.from, it.to)
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
        return checkEvaluator.isKingChecked(color, this.moveCalculator)
    }
}
