package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.Type
import spock.lang.Specification

class BaseSpec extends Specification {

    static def move(Piece piece, String expression) {
        def tokens = expression.split(" ")
        return new Move(piece, tokens[0], tokens[1])
    }

    static def aWhitePawn() {
        return new Piece(Color.WHITE, Type.PAWN)
    }
}
