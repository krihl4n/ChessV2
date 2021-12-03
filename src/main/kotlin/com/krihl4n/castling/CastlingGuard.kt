package com.krihl4n.castling

import com.krihl4n.command.MoveObserver
import com.krihl4n.model.*

class CastlingGuard : MoveObserver {

    private var whiteKingShortCastlingAllowed = true
    private var whiteKingLongCastlingAllowed = true

    private var blackKingShortCastlingAllowed = true
    private var blackKingLongCastlingAllowed = true

    override fun movePerformed(move: Move) {
        if (whiteKingShortCastlingAllowed && move.piece == Piece(Color.WHITE, Type.KING)) {
            whiteKingShortCastlingAllowed = false
            whiteKingLongCastlingAllowed = false
        }

        if (blackKingShortCastlingAllowed && move.piece == Piece(Color.BLACK, Type.KING)) {
            blackKingShortCastlingAllowed = false
            blackKingLongCastlingAllowed = false
        }

        if (whiteKingShortCastlingAllowed && move.piece == Piece(Color.WHITE, Type.ROOK) && move.from == Field("h1")) {
            whiteKingShortCastlingAllowed = false
        }

        if (whiteKingShortCastlingAllowed && move.piece == Piece(Color.WHITE, Type.ROOK) && move.from == Field("a1")) {
            whiteKingLongCastlingAllowed = false
        }

        if (blackKingShortCastlingAllowed && move.piece == Piece(Color.BLACK, Type.ROOK) && move.from == Field("h8")) {
            blackKingShortCastlingAllowed = false
        }

        if (blackKingLongCastlingAllowed && move.piece == Piece(Color.BLACK, Type.ROOK) && move.from == Field("a8")) {
            blackKingLongCastlingAllowed = false
        }
    }

    override fun moveUndid(move: Move) {
        TODO("Not yet implemented")
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
}