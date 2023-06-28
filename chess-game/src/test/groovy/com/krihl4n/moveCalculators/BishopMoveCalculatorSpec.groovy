package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.game.GameMode
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.moveCommands.CommandCoordinator
import spock.lang.Subject

class BishopMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        CalculatorFactory calculatorFactory = new CalculatorFactory()
        positionTracker = new PositionTracker()
        calculatorFactory.initCalculators(new EnPassantGuard(positionTracker, new CommandCoordinator(GameMode.TEST_MODE)), new CastlingGuard(positionTracker, calculatorFactory))
        calculator = new PieceMoveCalculator(positionTracker, calculatorFactory)
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
