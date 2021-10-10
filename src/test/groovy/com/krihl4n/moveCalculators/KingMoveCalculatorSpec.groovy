package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import spock.lang.Subject

class KingMoveCalculatorSpec extends BaseSpec {


    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        def factory = new CalculatorFactory(positionTracker)
        calculator = new PieceMoveCalculator(positionTracker, factory)
    }

    def "should calculate basic moves for the king"() {
        given:
        positionTracker.setPieceAtField(king, aField("e5"))
        positionTracker.setPieceAtField(friendlyPiece, aField("e4"))
        positionTracker.setPieceAtField(enemyPiece, aField("e6"))

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
        king         | friendlyPiece | enemyPiece
        aWhiteKing() | aWhitePawn()  | aBlackPawn()
        aBlackKing() | aBlackPawn()  | aWhitePawn()
    }
}
