package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.Dependencies
import com.krihl4n.PositionTracker
import spock.lang.Subject

class KingMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        new Dependencies()
        new CalculatorFactory()
        positionTracker = Dependencies.positionTracker
        calculator = new PieceMoveCalculator(positionTracker)
    }

    def "should calculate basic moves for the king"() {
        given:
        positionTracker.setPieceAtField(king, aField("e5"))
        positionTracker.setPieceAtField(friendlyPiece, aField("e4"))

        when:
        def moves = calculator.findMoves(aField("e5"))

        then:
        moves == [
                possibleMove("e5", "d5"),
                possibleMove("e5", "d6"),
                possibleMove("e5", "e6"),
                possibleMove("e5", "f6"),
                possibleMove("e5", "f5"),
                possibleMove("e5", "f4"),
                possibleMove("e5", "d4")
        ] as Set

        where:
        king         | friendlyPiece
        aWhiteKing() | aWhitePawn()
        aBlackKing() | aBlackPawn()
    }
}
