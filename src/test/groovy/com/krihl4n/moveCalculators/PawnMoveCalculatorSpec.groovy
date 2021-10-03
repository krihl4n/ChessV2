package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import spock.lang.Subject

class PawnMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker
    @Subject
    def calculator

    void setup() {
        positionTracker = new PositionTracker()
        calculator = new PawnMoveCalculator(positionTracker)
    }

    def "a white pawn can move forward in correct direction #start -> #finish"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField(start))

        when:
        def moves = calculator.calculateMoves(aField(start))

        then:
        moves.contains(possibleMove(start, finish))

        where:
        start | finish
        "a5"  | "a6"
        "b5"  | "b6"
        "c5"  | "c6"
        "d5"  | "d6"
        "e5"  | "e6"
        "f5"  | "f6"
        "g5"  | "g6"
        "h5"  | "h6"
    }

    def "a black pawn can move forward in correct direction #start -> #finish"() {
        given:
        positionTracker.setPieceAtField(aBlackPawn(), aField(start))

        when:
        def moves = calculator.calculateMoves(aField(start))

        then:
        moves.contains(possibleMove(start, finish))

        where:
        start | finish
        "a6"  | "a5"
        "b6"  | "b5"
        "c6"  | "c5"
        "d6"  | "d5"
        "e6"  | "e5"
        "f6"  | "f5"
        "g6"  | "g5"
        "h6"  | "h5"
    }

    def "a pawn cannot move from sideways"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("b2"))

        when:
        def moves = calculator.calculateMoves(aField("b2"))

        then:
        moves.each { move ->
            move.to.file == new com.krihl4n.model.File("b")
        }
    }


    def "should return empty list if piece is on last rank"() {
        given:
        positionTracker.setPieceAtField(pawn, field)

        when:
        def moves = calculator.calculateMoves(field)

        then:
        moves.isEmpty()

        where:
        pawn            | field
        aWhitePawn()    | aField("b8")
        aBlackPawn()    | aField("b1")
    }

    static def possibleMove(String from, String to) {
        new PossibleMove(new Field(from), new Field(to))
    }
}
