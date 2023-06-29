package com.krihl4n

import com.krihl4n.game.GameMode
import com.krihl4n.game.GameResult
import com.krihl4n.game.Result
import com.krihl4n.game.ResultReason

class DrawSpec extends BaseGameSpec {

    // https://support.chess.com/article/128-what-does-insufficient-mating-material-mean

    void setup() {
        gameCanBeFinished()
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
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

    def "insufficient material with two kings"() {
        given:
        setupPieces("wk_a1 bq_a2 bk_h1")

        when:
        performMove("a1 a2")

        then:
        assertPositions("wk_a2 bk_h1")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.INSUFFICIENT_MATERIAL)
    }

    def "insufficient material with two kings and bishop"() {
        given:
        setupPieces("wk_a1 wb_h2 bq_a2 bk_h1")

        when:
        performMove("a1 a2")

        then:
        assertPositions("wk_a2 wb_h2 bk_h1")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.INSUFFICIENT_MATERIAL)
    }
}
