package com.krihl4n

import com.krihl4n.game.GameMode

class EnPassantSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "en passant not possible if enemy pawn didn't move"() {
        given:
        setupPieces("wp_e5 bp_f5")

        when:
        performMove("e5 f6")

        then:
        assertPositions("wp_e5 bp_f5")
    }

    def "should be able to do en passant"() {
        given:
        setupPieces("wp_e5 bp_f7")

        when:
        performMove("f7 f5")
        performMove("e5 f6")

        then:
        assertPositions("wp_f6")
    }

    def "should be able to do en passant from both sides - left"() {
        given:
        setupPieces("wp_e5 bp_f7 wp_g5")

        when:
        performMove("f7 f5")
        performMove("e5 f6")

        then:
        assertPositions("wp_f6 wp_g5")
    }

    def "should be able to do en passant from both sides - right"() {
        given:
        setupPieces("wp_e5 bp_f7 wp_g5")

        when:
        performMove("f7 f5")
        performMove("g5 f6")

        then:
        assertPositions("wp_f6 wp_e5")
    }

    def "en passant not possible if another move performed"() {
        given:
        setupPieces("wp_e5 bp_f7 wq_a1 bq_a2")

        when:
        performMove("f7 f5")
        performMove("a1 b1")
        performMove("a2 b2")
        performMove("e5 f6")

        then:
        assertPositions("bp_f5 wp_e5 wq_b1 bq_b2")
    }

    def "undo en passant"() {
        given:
        setupPieces("wp_e5 bp_f7")

        when:
        performMove("f7 f5")
        performMove("e5 f6")
        undoMove("player")

        then:
        assertPositions("wp_e5 bp_f5")
    }

    def "should be able to do en passant at the edge of chessboard"() {
        given:
        setupPieces("wp_b5 bp_a7")

        when:
        performMove("a7 a5")
        performMove("b5 a6")

        then:
        assertPositions("wp_a6")
    }

    def "should be able to do en passant with black pawn"() {
        given:
        setupPieces("wp_e2 bp_f4")

        when:
        performMove("e2 e4")
        performMove("f4 e3")

        then:
        assertPositions("bp_e3")
    }

    def "en passant bug fix"() {
        given:
        setupPieces("wp_e2 wp_f2")
        performMove("e2 e4")

        when:
        performMove("f2 f4")

        then:
        !game.getPossibleMoves("f4").to.contains("f3")
    }

    def "allow en passant for correct files only"() {
        given:
        setupPieces("wp_d2 bp_e4")

        when:
        performMove("d2 d4")

        then:
        !game.getPossibleMoves("d4").to.contains("d3")
    }
}
