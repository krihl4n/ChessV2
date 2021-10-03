package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import spock.lang.Subject

class PieceMoveCalculatorSpec extends BaseSpec {

    PositionTracker positionTracker = new PositionTracker()

    @Subject
    def calculator

    void setup() {
        calculator = new PieceMoveCalculator(positionTracker, new CalculatorFactory())
    }

    def "should throw exception if there is no piece at field" () {
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
