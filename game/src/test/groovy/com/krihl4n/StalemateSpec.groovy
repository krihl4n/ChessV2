package com.krihl4n

import com.krihl4n.game.GameResult
import com.krihl4n.game.Result
import com.krihl4n.game.ResultReason

class StalemateSpec extends BaseGameSpec {

    void setup() {
        gameCanBeFinished()
        game.initialize()
        game.playerReady(null)
    }

    def "stalemate should result in a draw"() {
        given:
        setupPieces("wk_f6 wb_g7 bk_h7")

        when:
        performMove("f6 f7")

        then:
        assertPositions("wk_f7 wb_g7 bk_h7")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.STALEMATE)
    }
}
