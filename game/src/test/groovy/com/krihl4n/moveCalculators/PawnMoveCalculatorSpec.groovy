package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.guards.EnPassantGuard
import spock.lang.Subject

class PawnMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker
    @Subject
    MoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        calculator = new PawnMoveCalculator(new EnPassantGuard(positionTracker, new CommandCoordinator("")))
    }

    def "a pawn can move one field forward"() {
        given:
        positionTracker.setPieceAtField(pawn, field)

        when:
        def moves = calculator.calculateMoves(field, positionTracker)

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
        def moves = calculator.calculateMoves(field, positionTracker)

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
        def moves = calculator.calculateMoves(field, positionTracker)

        then:
        moves == expectedFields

        where:
        pawn         | field        || expectedFields
        aWhitePawn() | aField("b2") || [possibleMove("b2", "b3"), possibleMove("b2", "b4")] as Set
        aBlackPawn() | aField("b7") || [possibleMove("b7", "b6"), possibleMove("b7", "b5")] as Set
    }

    def "cannot move forward if blocked by another piece"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("b2"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("b3"))

        when:
        def moves = calculator.calculateMoves(aField("b2"), positionTracker)

        then:
        moves.isEmpty()
    }

    def "cannot move 2 fields forward if blocked by another piece"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("b2"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("b4"))

        when:
        def moves = calculator.calculateMoves(aField("b2"), positionTracker)

        then:
        !moves.contains(possibleMove("b2", "b4"))
    }

    def "pawns can attack diagonally" () {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("b2"))
        positionTracker.setPieceAtField(aBlackPawn(), aField("c3"))
        positionTracker.setPieceAtField(aBlackPawn(), aField("a3"))

        when:
        def moves = calculator.calculateMoves(aField("b2"), positionTracker)

        then:
        moves.contains(possibleMove("b2", "c3"))
        moves.contains(possibleMove("b2", "a3"))
    }

    def "cannot attack pieces of same color" () {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("b2"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("c3"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("a3"))

        when:
        def moves = calculator.calculateMoves(aField("b2"), positionTracker)

        then:
        !moves.contains(possibleMove("b2", "c3"))
        !moves.contains(possibleMove("b2", "a3"))
    }

    def "black pawns can attack diagonally" () {
        given:
        positionTracker.setPieceAtField(aBlackPawn(), aField("b7"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("c6"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("a6"))

        when:
        def moves = calculator.calculateMoves(aField("b7"), positionTracker)

        then:
        moves.contains(possibleMove("b7", "c6"))
        moves.contains(possibleMove("b7", "a6"))
    }
}
