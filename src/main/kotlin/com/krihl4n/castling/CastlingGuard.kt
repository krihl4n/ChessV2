package com.krihl4n.castling

import com.krihl4n.command.MoveObserver
import com.krihl4n.model.*

class CastlingGuard: MoveObserver {

    private var whiteKingShortCastlingAllowed = true

    override fun movePerformed(move: Move) {
        if (whiteKingShortCastlingAllowed && move.piece == Piece(Color.WHITE, Type.ROOK) && move.from == Field("h1")) {
            whiteKingShortCastlingAllowed = false
        }
    }

    override fun moveUndid(move: Move) {
        TODO("Not yet implemented")
    }

    fun canWhiteKingShortCastle(): Boolean {
        return whiteKingShortCastlingAllowed
    }
}