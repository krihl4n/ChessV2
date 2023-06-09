package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.moveCommands.CommandCoordinator
import spock.lang.Subject

class QueenMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        CalculatorFactory calculatorFactory = new CalculatorFactory()
        positionTracker = new PositionTracker()
        calculatorFactory.initCalculators(new EnPassantGuard(positionTracker, new CommandCoordinator()), new CastlingGuard(positionTracker, calculatorFactory))
        calculator = new PieceMoveCalculator(positionTracker, calculatorFactory)
    }

    def "test queen moves"() {
        given:
        positionTracker.setPieceAtField(aBlackQueen(), aField("a8"))

        when:
        def moves = calculator.findMoves(aField("a8"))

        then:
        moves == [
                possibleMove("a8", "b8"),
                possibleMove("a8", "c8"),
                possibleMove("a8", "d8"),
                possibleMove("a8", "e8"),
                possibleMove("a8", "f8"),
                possibleMove("a8", "g8"),
                possibleMove("a8", "h8"),
                possibleMove("a8", "a7"),
                possibleMove("a8", "a6"),
                possibleMove("a8", "a5"),
                possibleMove("a8", "a4"),
                possibleMove("a8", "a3"),
                possibleMove("a8", "a2"),
                possibleMove("a8", "a1"),
                possibleMove("a8", "b7"),
                possibleMove("a8", "c6"),
                possibleMove("a8", "d5"),
                possibleMove("a8", "e4"),
                possibleMove("a8", "f3"),
                possibleMove("a8", "g2"),
                possibleMove("a8", "h1"),
        ] as Set
    }
}
