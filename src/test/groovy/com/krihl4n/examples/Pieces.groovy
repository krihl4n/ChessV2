package com.krihl4n.examples

import com.krihl4n.model.Color
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class Pieces {

    static def aWhitePawn() {
        return new Piece(Color.WHITE, Type.PAWN)
    }
}
