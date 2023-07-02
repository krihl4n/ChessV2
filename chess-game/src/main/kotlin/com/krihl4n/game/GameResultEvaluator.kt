package com.krihl4n.game

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.game.Result.*
import com.krihl4n.game.ResultReason.*
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.*
import com.krihl4n.model.Color.*
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
                    if (it == WHITE) {
                        GameResult(WHITE_PLAYER_WON, CHECK_MATE)
                    } else {
                        GameResult(BLACK_PLAYER_WON, CHECK_MATE)
                    }
                }
                notifyGameFinished()
                return
            }
        }
        if (noMoreValidMovesFor(move.piece.color.opposite())) {
            this.result = GameResult(DRAW, STALEMATE)
            notifyGameFinished()
            return
        }
        if (insufficientMaterial()) {
            this.result = GameResult(DRAW, INSUFFICIENT_MATERIAL)
            notifyGameFinished()
            return
        }
        if(fiftyMoveRepetition(move)) {
            this.result = GameResult(DRAW, REPETITION)
            notifyGameFinished()
            return
        }
    }

    private var moveRepetitionCounter = 0
    private fun fiftyMoveRepetition(move: Move): Boolean {
       if(move.piece.type == PAWN){ // check if attack
        moveRepetitionCounter = 0
       }
       moveRepetitionCounter++
       println(moveRepetitionCounter)
       return moveRepetitionCounter == 100
    }

    private fun insufficientMaterial(): Boolean {
        val piecesLeft = positionTracker.getPositionsOfAllPieces().map { it.value }

        return piecesLeft
            .hasPieces(
                Piece(WHITE, KING),
                Piece(BLACK, KING)
            ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, BISHOP),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(BLACK, BISHOP),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, KNIGHT),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(BLACK, KNIGHT),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, BISHOP),
                    Piece(BLACK, BISHOP),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, KNIGHT),
                    Piece(BLACK, KNIGHT),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, KNIGHT),
                    Piece(BLACK, BISHOP),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(BLACK, KNIGHT),
                    Piece(WHITE, BISHOP),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(BLACK, KNIGHT),
                    Piece(BLACK, KNIGHT),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(WHITE, KNIGHT),
                    Piece(WHITE, KNIGHT),
                    Piece(WHITE, KING),
                    Piece(BLACK, KING)
                )
    }

    private fun List<Piece>.hasPieces(vararg pieces: Piece): Boolean {
        if (this.size != pieces.size) return false
        val piecesList = this.toMutableList()
        for (piece in pieces) {
            val index = piecesList.indexOfFirst { it == piece }
            if (index == -1) return false
            piecesList.removeAt(index)
        }
        return piecesList.size == 0
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

    fun resign(resigningPlayerColor: Color) {
        when (resigningPlayerColor) {
            WHITE -> this.result = GameResult(BLACK_PLAYER_WON, PLAYER_RESIGNED)
            BLACK -> this.result = GameResult(WHITE_PLAYER_WON, PLAYER_RESIGNED)
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
