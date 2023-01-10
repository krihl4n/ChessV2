package com.krihl4n

import com.krihl4n.game.GameResult
import com.krihl4n.game.Result
import com.krihl4n.game.ResultReason

class DeadPositionSpec extends BaseGameSpec {

    void setup() {
        gameCanBeFinished()
        game.initialize()
        game.playerReady(null)
    }

    def "dead position when just two kings left"() {
        given:
        setupPieces("wk_f6 bk_h7 bq_f5")

        when:
        performMove("f6 f5")

        then:
        assertPositions("wk_f5 bk_h7")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.DEAD_POSITION)
    }
}
