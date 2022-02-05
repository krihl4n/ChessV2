package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.Type
import com.krihl4n.moveCalculators.PossibleMove
import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
class BaseSpec extends Specification {

    static def move(Piece piece, String expression) {
        def tokens = expression.split(" ")
        return new Move(piece, tokens[0], tokens[1])
    }

    static def aWhitePawn() {
        return new Piece(Color.@WHITE, Type.PAWN)
    }

    static def aBlackPawn() {
        return new Piece(Color.@BLACK, Type.PAWN)
    }

    static def aWhiteRook() {
        return new Piece(Color.@WHITE, Type.ROOK)
    }

    static def aBlackRook() {
        return new Piece(Color.@BLACK, Type.ROOK)
    }

    static def aWhiteKing() {
        return new Piece(Color.@WHITE, Type.KING)
    }

    static def aBlackKing() {
        return new Piece(Color.@BLACK, Type.KING)
    }

    static def aWhiteKnight() {
        return new Piece(Color.@WHITE, Type.KNIGHT)
    }

    static def aBlackKnight() {
        return new Piece(Color.@BLACK, Type.KNIGHT)
    }

    static def aWhiteBishop() {
        return new Piece(Color.@WHITE, Type.BISHOP)
    }

    static def aBlackQueen() {
        return new Piece(Color.@BLACK, Type.QUEEN)
    }

    static def aField() {
        return new Field("b2")
    }

    static def aField(String token) {
        return new Field(token)
    }

    static def possibleMove(String from, String to) {
        new PossibleMove(new Field(from), new Field(to))
    }
}
