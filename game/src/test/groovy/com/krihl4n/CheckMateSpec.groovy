package com.krihl4n

import com.krihl4n.game.GameResult
import com.krihl4n.game.Result
import com.krihl4n.game.ResultReason

class CheckMateSpec extends BaseGameSpec {

    void setup() {
        gameCanBeFinished()
        game.start()
        game.registerPlayer("player", null)
    }

    def "game should be finished if check mate"() {
        given:
        setupPieces("bk_a8 wr_h7 wr_g7")

        when:
        performMove("g7 g8")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.WHITE_PLAYER_WON, ResultReason.CHECK_MATE)
    }

    def "should be able to save with king escape"() {
        given:
        setupPieces("bk_a8 wr_h7")

        when:
        performMove("h7 h8")

        then:
        assertPositions("bk_a8 wr_h8")

        and:
        !game.isGameFinished()
    }

    def "should be able to save king with block"() {
        given:
        setupPieces("wk_a1 wp_b1 wp_b2 wq_b2 bq_b8")

        when:
        performMove("b8 a8")

        then:
        assertPositions("wk_a1 wp_b1 wp_b2 wq_b2 bq_a8")

        and:
        !game.isGameFinished()
    }

    def "should be able to save king with attack by different piece"() {
        given:
        setupPieces("wk_a1 wp_b1 wp_b2 wb_b7 bq_b8")

        when:
        performMove("b8 a8")

        then:
        assertPositions("wk_a1 wp_b1 wp_b2 wb_b7 bq_a8")

        and:
        !game.isGameFinished()
    }

    def "should be able to save with king attack"() {
        given:
        setupPieces("wk_a1 wp_a2 bq_b7")

        when:
        performMove("b7 b1")

        then:
        assertPositions("wk_a1 wp_a2 bq_b1")

        and:
        !game.isGameFinished()
    }

    def "check mate if king can attack but piece protected"() {
        given:
        setupPieces("wk_a1 wp_a2 bq_b7 br_b8")

        when:
        performMove("b7 b1")

        then:
        assertPositions("wk_a1 wp_a2 bq_b1 br_b8")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.BLACK_PLAYER_WON, ResultReason.CHECK_MATE)
    }

    def "check mate delivered with castling"() {
        given:
        setupPieces("bp_e8 bk_f8 bp_g8 bp_e7 bp_g7 wk_e1 wr_h1")

        when:
        performMove("e1 g1")

        then:
        assertPositions("bp_e8 bk_f8 bp_g8 bp_e7 bp_g7 wk_g1 wr_f1")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.WHITE_PLAYER_WON, ResultReason.CHECK_MATE)
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
