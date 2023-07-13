package com.krihl4n

import com.krihl4n.game.GameMode
import com.krihl4n.game.guards.CastlingGuard
import com.krihl4n.game.positionEvaluators.CheckEvaluator
import com.krihl4n.game.guards.EnPassantGuard
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCommands.CommandCoordinator
import spock.lang.Subject

class MoveValidatorSpec extends BaseSpec {

    PositionTracker positionTracker
    @Subject
    MoveValidator moveValidator

    void setup() {
        CalculatorFactory calculatorFactory = new CalculatorFactory()
        positionTracker = new PositionTracker()
        calculatorFactory.initCalculators(new EnPassantGuard(positionTracker, new CommandCoordinator(GameMode.TEST_MODE)), new CastlingGuard(positionTracker, calculatorFactory))
        PieceMoveCalculator calculator = new PieceMoveCalculator(positionTracker, calculatorFactory)
        moveValidator = new MoveValidator(calculator, new CheckEvaluator(positionTracker, calculator))
    }

    def "should allow not allow performing illegal moves"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a2"))

        when:
        def result = moveValidator.isMoveValid(move(aWhitePawn(), "a2 b2"))

        then:
        !result
    }

    def "should be able to perform legal moves"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a2"))

        when:
        def result = moveValidator.isMoveValid(move(aWhitePawn(), "a2 a3"))

        then:
        result
    }
}
