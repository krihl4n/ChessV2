package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.castling.CastlingGuard
import spock.lang.Subject

class RookMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        def factory = new CalculatorFactory(positionTracker, new CastlingGuard())
        calculator = new PieceMoveCalculator(positionTracker, factory)
    }

    def "should give all fields to the right"() {
        given:
        positionTracker.setPieceAtField(rook, aField("a1"))

        when:
        def moves = calculator.findMoves(aField("a1"))

        then:
        moves.containsAll([
                possibleMove("a1", "b1"),
                possibleMove("a1", "c1"),
                possibleMove("a1", "d1"),
                possibleMove("a1", "e1"),
                possibleMove("a1", "f1"),
                possibleMove("a1", "g1"),
                possibleMove("a1", "h1")
        ])

        where:
        rook << [aWhiteRook(), aBlackRook()]
    }

    def "should give all fields to the right and to the left"() {
        given:
        positionTracker.setPieceAtField(rook, aField("d1"))

        when:
        def moves = calculator.findMoves(aField("d1"))

        then:
        moves.containsAll([
                possibleMove("d1", "a1"),
                possibleMove("d1", "b1"),
                possibleMove("d1", "c1"),
                possibleMove("d1", "e1"),
                possibleMove("d1", "f1"),
                possibleMove("d1", "g1"),
                possibleMove("d1", "h1")
        ])

        where:
        rook << [aWhiteRook(), aBlackRook()]
    }

    def "should give all fields up and down" () {
        given:
        positionTracker.setPieceAtField(rook, aField("a4"))

        when:
        def moves = calculator.findMoves(aField("a4"))

        then:
        moves.containsAll([
                possibleMove("a4", "a1"),
                possibleMove("a4", "a2"),
                possibleMove("a4", "a3"),
                possibleMove("a4", "a5"),
                possibleMove("a4", "a6"),
                possibleMove("a4", "a7"),
                possibleMove("a4", "a8")
        ])

        where:
        rook << [aWhiteRook(), aBlackRook()]
    }

    def "should not be able to move if surrounded by same color pieces"() {
        given:
        positionTracker.setPieceAtField(rook, aField("d5"))
        positionTracker.setPieceAtField(pawn, aField("d4"))
        positionTracker.setPieceAtField(pawn, aField("d6"))
        positionTracker.setPieceAtField(pawn, aField("c5"))
        positionTracker.setPieceAtField(pawn, aField("e5"))

        when:
        def moves = calculator.findMoves(aField("d5"))

        then:
        moves.isEmpty()

        where:
        rook         | pawn
        aWhiteRook() | aWhitePawn()
        aBlackRook() | aBlackPawn()
    }

    def "should be able to attack other pieces"() {
        given:
        positionTracker.setPieceAtField(rook, aField("d5"))
        positionTracker.setPieceAtField(pawn, aField("d4"))
        positionTracker.setPieceAtField(pawn, aField("d6"))
        positionTracker.setPieceAtField(pawn, aField("c5"))
        positionTracker.setPieceAtField(pawn, aField("e5"))

        when:
        def moves = calculator.findMoves(aField("d5"))

        then:
        moves == [
                possibleMove("d5", "d4"),
                possibleMove("d5", "d6"),
                possibleMove("d5", "c5"),
                possibleMove("d5", "e5"),
        ] as Set

        where:
        rook         | pawn
        aWhiteRook() | aBlackPawn()
        aBlackRook() | aWhitePawn()
    }
}
