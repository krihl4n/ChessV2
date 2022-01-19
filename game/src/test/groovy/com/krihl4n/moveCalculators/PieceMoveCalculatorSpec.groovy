package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.command.CommandCoordinator
import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.EnPassantGuard
import spock.lang.Subject

class PieceMoveCalculatorSpec extends BaseSpec {

    def positionTracker

    @Subject
    def calculator

    void setup() {
        CalculatorFactory calculatorFactory = new CalculatorFactory()
        positionTracker = new PositionTracker()
        calculatorFactory.initCalculators(new EnPassantGuard(positionTracker, new CommandCoordinator()), new CastlingGuard(positionTracker, calculatorFactory))
        calculator = new PieceMoveCalculator(positionTracker, calculatorFactory)
    }

    def "should throw exception if there is no piece at field"() {
        given:
        positionTracker.removePieceFromField(aField())

        when:
        calculator.findMoves(aField())

        then:
        thrown(IllegalArgumentException)
    }

    def "should return a collection of possible moves"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField())

        when:
        def result = calculator.findMoves(aField())

        then:
        result instanceof Set<PossibleMove>
    }
}
