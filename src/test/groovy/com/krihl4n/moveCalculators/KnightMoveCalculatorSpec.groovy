package com.krihl4n.moveCalculators

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import com.krihl4n.castling.CastlingGuard
import com.krihl4n.moveCalculators.filters.PossibleMoveFilter
import spock.lang.Subject

class KnightMoveCalculatorSpec extends BaseSpec {


    PositionTracker positionTracker

    @Subject
    PieceMoveCalculator calculator

    void setup() {
        positionTracker = new PositionTracker()
        def factory = new CalculatorFactory(positionTracker, new CastlingGuard())
        calculator = new PieceMoveCalculator(positionTracker, factory, new HashSet<PossibleMoveFilter>())
    }

    def "should move correctly from corners"() {
        given:
        positionTracker.setPieceAtField(aWhiteKnight(), start)

        when:
        def moves = calculator.findMoves(start)

        then:
        moves == expectedMoves as Set

        where:
        start        || expectedMoves
        aField("a1") || [possibleMove("a1", "c2"), possibleMove("a1", "b3")]
        aField("a8") || [possibleMove("a8", "c7"), possibleMove("a8", "b6")]
        aField("h1") || [possibleMove("h1", "f2"), possibleMove("h1", "g3")]
        aField("h8") || [possibleMove("h8", "f7"), possibleMove("h8", "g6")]
    }
}
