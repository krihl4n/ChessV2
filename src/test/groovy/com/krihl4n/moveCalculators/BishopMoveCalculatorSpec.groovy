package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.castling.CastlingGuard
import com.krihl4n.moveCalculators.filters.PossibleMoveFilter
import spock.lang.Subject

class BishopMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        def factory = new CalculatorFactory(positionTracker, new CastlingGuard())
        calculator = new PieceMoveCalculator(positionTracker, factory, new HashSet<PossibleMoveFilter>())
    }

    def "test bishop moves"() {
        given:
        positionTracker.setPieceAtField(aWhiteBishop(), aField("b2"))
        positionTracker.setPieceAtField(aWhitePawn(), aField("a1"))
        positionTracker.setPieceAtField(aBlackPawn(), aField("h8"))

        when:
        def moves = calculator.findMoves(aField("b2"))

        then:
        moves == [
                possibleMove("b2", "a3"),
                possibleMove("b2", "c1"),
                possibleMove("b2", "c3"),
                possibleMove("b2", "d4"),
                possibleMove("b2", "e5"),
                possibleMove("b2", "f6"),
                possibleMove("b2", "g7"),
                possibleMove("b2", "h8"),
        ] as Set
    }
}
