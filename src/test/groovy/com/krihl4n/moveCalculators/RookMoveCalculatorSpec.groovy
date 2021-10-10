package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import spock.lang.Subject

class RookMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        def factory = new CalculatorFactory(positionTracker)
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
}
