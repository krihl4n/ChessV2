package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
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

    static def aBlackPawn() {
        return new Piece(Color.BLACK, Type.PAWN)
    }

    static def aField() {
        return new Field("b2")
    }

    static def aField(String token) {
        return new Field(token)
    }
}
