package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.Dependencies
import spock.lang.Subject

class PieceMoveCalculatorSpec extends BaseSpec {

    def positionTracker

    @Subject
    def calculator

    void setup() {
        new Dependencies()
        new CalculatorFactory()
        positionTracker = Dependencies.positionTracker
        calculator = new PieceMoveCalculator(positionTracker)
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
