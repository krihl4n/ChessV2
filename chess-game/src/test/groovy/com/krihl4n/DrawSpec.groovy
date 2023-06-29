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

    def "insufficient material for #scenario"() {
        given:
        setupPieces(setup)

        when:
        performMove("a1 a2")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.INSUFFICIENT_MATERIAL)

        where:
        scenario                                | setup
        "two kings"                             | "wk_a1 bq_a2 bk_h1"
        "two kings, black bishop"               | "wk_a1 bq_a2 bk_h1 bb_h2"
        "two kings, white bishop"               | "wk_a1 bq_a2 bk_h1 wb_h2"
        "two kings, black knight"               | "wk_a1 bq_a2 bk_h1 wn_h2"
        "two kings, white knight"               | "wk_a1 bq_a2 bk_h1 wn_h2"
        "two kings, white bishop, black bishop" | "wk_a1 bq_a2 bk_h1 wb_h2 bb_h3"
        "two kings, white knight, black knight" | "wk_a1 bq_a2 bk_h1 wn_h2 bn_h3"
        "two kings, white bishop, black knight" | "wk_a1 bq_a2 bk_h1 wb_h2 bn_h3"
        "two kings, black bishop, white knight" | "wk_a1 bq_a2 bk_h1 bb_h2 wn_h3"
        "two kings, two white knights"          | "wk_a1 bq_a2 bk_h1 wn_h2 wn_h3"
        "two kings, two black knights"          | "wk_a1 bq_a2 bk_h1 bn_h2 bn_h3"
    }
}
