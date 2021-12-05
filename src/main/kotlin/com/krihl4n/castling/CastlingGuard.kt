package com.krihl4n.castling

import com.krihl4n.command.MoveObserver
import com.krihl4n.model.*

private const val WHITE_ROOK_KING_SIDE_STARTING_POS = "h1"
private const val WHITE_ROOK_QUEEN_SIDE_STARTING_POS = "a1"
private const val BLACK_ROOK_KING_SIDE_STARTING_POS = "h8"
private const val BLACK_ROOK_QUEEN_SIDE_STARTING_POS = "a8"

class CastlingGuard : MoveObserver {

    private var whiteKingShortCastlingAllowed = true
    private var whiteKingLongCastlingAllowed = true
    private var blackKingShortCastlingAllowed = true
    private var blackKingLongCastlingAllowed = true

    override fun movePerformed(move: Move) {
        if (whiteKingShortCastlingAllowed && move.whiteKingMoved()) {
            whiteKingShortCastlingAllowed = false
            whiteKingLongCastlingAllowed = false
        }

        if (blackKingShortCastlingAllowed && move.blackKingMoved()) {
            blackKingShortCastlingAllowed = false
            blackKingLongCastlingAllowed = false
        }

        if (whiteKingShortCastlingAllowed && move.whiteRookMovedFrom(WHITE_ROOK_KING_SIDE_STARTING_POS)) {
            whiteKingShortCastlingAllowed = false
        }

        if (whiteKingShortCastlingAllowed && move.whiteRookMovedFrom(WHITE_ROOK_QUEEN_SIDE_STARTING_POS)) {
            whiteKingLongCastlingAllowed = false
        }

        if (blackKingShortCastlingAllowed && move.blackRookMovedFrom(BLACK_ROOK_KING_SIDE_STARTING_POS)) {
            blackKingShortCastlingAllowed = false
        }

        if (blackKingLongCastlingAllowed && move.blackRookMovedFrom(BLACK_ROOK_QUEEN_SIDE_STARTING_POS)) {
            blackKingLongCastlingAllowed = false
        }
    }

    override fun moveUndid(move: Move) {
        if (!whiteKingShortCastlingAllowed && move.whiteKingMoved()) {
            whiteKingShortCastlingAllowed = true
            whiteKingLongCastlingAllowed = true
        }

        if (!blackKingShortCastlingAllowed && move.blackKingMoved()) {
            blackKingShortCastlingAllowed = true
            blackKingLongCastlingAllowed = true
        }

        if (!whiteKingShortCastlingAllowed && move.whiteRookMovedFrom(WHITE_ROOK_KING_SIDE_STARTING_POS)) {
            whiteKingShortCastlingAllowed = true
        }

        if (!whiteKingShortCastlingAllowed && move.whiteRookMovedFrom(WHITE_ROOK_QUEEN_SIDE_STARTING_POS)) {
            whiteKingLongCastlingAllowed = true
        }

        if (!blackKingShortCastlingAllowed && move.blackRookMovedFrom(BLACK_ROOK_KING_SIDE_STARTING_POS)) {
            blackKingShortCastlingAllowed = true
        }

        if (!blackKingLongCastlingAllowed && move.blackRookMovedFrom(BLACK_ROOK_QUEEN_SIDE_STARTING_POS)) {
            blackKingLongCastlingAllowed = true
        }
    }

    fun canWhiteKingShortCastle(): Boolean {
        return whiteKingShortCastlingAllowed
    }

    fun canWhiteKingLongCastle(): Boolean {
        return whiteKingLongCastlingAllowed
    }

    fun canBlackKingShortCastle(): Boolean {
        return blackKingShortCastlingAllowed
    }

    fun canBlackKingLongCastle(): Boolean {
        return blackKingLongCastlingAllowed
    }

    private fun Move.whiteRookMovedFrom(field: String) =
        this.piece == Piece(Color.WHITE, Type.ROOK) && this.from == Field(field)

    private fun Move.blackRookMovedFrom(field: String) =
        this.piece == Piece(Color.BLACK, Type.ROOK) && this.from == Field(field)

    private fun Move.whiteKingMoved() = this.piece == Piece(Color.WHITE, Type.KING)

    private fun Move.blackKingMoved() = this.piece == Piece(Color.BLACK, Type.KING)
}