package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.game.Result.*
import com.krihl4n.game.ResultReason.*
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.*
import com.krihl4n.model.Move
import com.krihl4n.model.Type.*
import com.krihl4n.moveCommands.MoveObserver

internal class GameResultEvaluator(
    val positionTracker: PositionTracker,
    private val moveValidator: MoveValidator,
    private val checkEvaluator: CheckEvaluator
) : MoveObserver {

    private val resultObservers = mutableListOf<GameResultObserver>()
    private var result: GameResult? = null

    override fun movePerformed(move: Move) {
        if (isKingChecked(move.piece.color.opposite())) {
            if (!isThereASavingMove(move.piece.color.opposite())) {
                this.result = move.piece.color.let {
                    if (it == Color.WHITE) {
                        GameResult(WHITE_PLAYER_WON, CHECK_MATE)
                    } else {
                        GameResult(BLACK_PLAYER_WON, CHECK_MATE)
                    }
                }
                notifyGameFinished()
            }
        } else if (noMoreValidMovesFor(move.piece.color.opposite())) {
            this.result = GameResult(DRAW, STALEMATE)
            notifyGameFinished()
        } else if (insufficientMaterial()) {
            this.result = GameResult(DRAW, INSUFFICIENT_MATERIAL)
            notifyGameFinished()
        }
    }

    private fun insufficientMaterial(): Boolean {
        val piecesLeft = positionTracker.getPositionsOfAllPieces().map { it.value }

        return piecesLeft.hasPieces(KING, KING) || piecesLeft.hasPieces(BISHOP, KING, KING)
    }

    private fun List<Piece>.hasPieces(vararg types: Type): Boolean {
        if(this.size != types.size) return false
        val pieces = this.toMutableList()
        for (t in types) {
            val index = pieces.indexOfFirst { it.type == t }
            if (index == -1) return false
            pieces.removeAt(index)
        }
        return pieces.size == 0
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

    fun resign(resigningPlayerColor: Color) {
        when (resigningPlayerColor) {
            Color.WHITE -> this.result = GameResult(BLACK_PLAYER_WON, PLAYER_RESIGNED)
            Color.BLACK -> this.result = GameResult(WHITE_PLAYER_WON, PLAYER_RESIGNED)
        }
        notifyGameFinished()
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
        this.result?.let { result -> resultObservers.forEach { it.gameFinished(result) } }
    }

    private fun isKingChecked(color: Color): Boolean {
        return checkEvaluator.isKingChecked(color)
    }
}
