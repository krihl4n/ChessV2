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

    def "a pawn can move one field forward"() {
        given:
        positionTracker.setPieceAtField(pawn, field)

        when:
        def moves = calculator.calculateMoves(field)

        then:
        moves == expectedFields

        where:
        pawn         | field        || expectedFields
        aWhitePawn() | aField("b3") || [possibleMove("b3", "b4")] as Set
        aBlackPawn() | aField("b6") || [possibleMove("b6", "b5")] as Set
    }

    def "should return empty list if piece is on last rank"() {
        given:
        positionTracker.setPieceAtField(pawn, field)

        when:
        def moves = calculator.calculateMoves(field)

        then:
        moves.isEmpty()

        where:
        pawn         | field
        aWhitePawn() | aField("b8")
        aBlackPawn() | aField("b1")
    }

    def "can move two fields forward from starting position"() {
        given:
        positionTracker.setPieceAtField(pawn, field)

        when:
        def moves = calculator.calculateMoves(field)

        then:
        moves == expectedFields

        where:
        pawn         | field        || expectedFields
        aWhitePawn() | aField("b2") || [possibleMove("b2", "b3"), possibleMove("b2", "b4")] as Set
        aBlackPawn() | aField("b7") || [possibleMove("b7", "b6"), possibleMove("b7", "b5")] as Set
    }

    static def possibleMove(String from, String to) {
        new PossibleMove(new Field(from), new Field(to))
    }
}
