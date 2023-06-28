package com.krihl4n

import com.krihl4n.game.GameMode

class CastlingSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "basic castling #type"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | move    || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 c1" || "wk_c1 wr_d1"
        "white king short" | "wk_e1 wr_h1" | "e1 g1" || "wk_g1 wr_f1"
        "black king long"  | "bk_e8 br_a8" | "e8 c8" || "bk_c8 br_d8"
        "black king short" | "bk_e8 br_h8" | "e8 g8" || "bk_g8 br_f8"
    }

    def "basic castling #type - undo move"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)
        undoMove("player")

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | move    || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 c1" || "wk_e1 wr_a1"
        "white king short" | "wk_e1 wr_h1" | "e1 g1" || "wk_e1 wr_h1"
        "black king long"  | "bk_e8 br_a8" | "e8 c8" || "bk_e8 br_a8"
        "black king short" | "bk_e8 br_h8" | "e8 g8" || "bk_e8 br_h8"
    }

    def "can't do castling of type #type if rook moved before"() {
        given:
        setupPieces(startingSetup)

        and:
        performMove(rookMove1)
        performMove(rookMove2)

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | rookMove1 | rookMove2 | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "a1 a2"   | "a2 a1"   | "e1 c1"      || startingSetup
        "white king short" | "wk_e1 wr_h1" | "h1 h2"   | "h2 h1"   | "e1 g1"      || startingSetup
        "black king long"  | "bk_e8 br_a8" | "a8 a7"   | "a7 a8"   | "e8 c8"      || startingSetup
        "black king short" | "bk_e8 br_h8" | "h8 h7"   | "h7 h8"   | "e8 g8"      || startingSetup
    }

    def "can't do castling of type #type if king moved before"() {
        given:
        setupPieces(startingSetup)

        and:
        performMove(kingMove1)
        performMove(kingMove2)

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | kingMove1 | kingMove2 | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 e2"   | "e2 e1"   | "e1 c1"      || startingSetup
        "white king short" | "wk_e1 wr_h1" | "e1 e2"   | "e2 e1"   | "e1 g1"      || startingSetup
        "black king long"  | "bk_e8 br_a8" | "e8 e7"   | "e7 e8"   | "e8 c8"      || startingSetup
        "black king short" | "bk_e8 br_h8" | "e8 e7"   | "e7 e8"   | "e8 g8"      || startingSetup
    }

    def "can do castling of type #type after undoing king move"() {
        given:
        setupPieces(startingSetup)

        and:
        performMove(kingMove)
        undoMove("player")

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | kingMove | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 e2"  | "e1 c1"      || "wk_c1 wr_d1"
        "white king short" | "wk_e1 wr_h1" | "e1 e2"  | "e1 g1"      || "wk_g1 wr_f1"
        "black king long"  | "bk_e8 br_a8" | "e8 e7"  | "e8 c8"      || "bk_c8 br_d8"
        "black king short" | "bk_e8 br_h8" | "e8 e7"  | "e8 g8"      || "bk_g8 br_f8"
    }

    def "undoing king move should not allow castling again if rook moved"() {
        given:
        setupPieces(startingSetup)

        and:
        performMove(rookMove1)
        performMove(rookMove2)
        performMove(kingMove)
        undoMove("player")

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | rookMove1 | rookMove2 | kingMove | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "a1 a2"   | "a2 a1"   | "e1 e2"  | "e1 c1"      || startingSetup
        "white king short" | "wk_e1 wr_h1" | "h1 h2"   | "h2 h1"   | "e1 e2"  | "e1 g1"      || startingSetup
        "black king long"  | "bk_e8 br_a8" | "a8 a7"   | "a7 a8"   | "e8 e7"  | "e8 c8"      || startingSetup
        "black king short" | "bk_e8 br_h8" | "h8 h7"   | "h7 h8"   | "e8 e7"  | "e8 g8"      || startingSetup
    }

    def "undoing repeated similar move should not permit castling again"() {
        given:
        setupPieces(startingSetup)

        and:
        performMove(kingMove1)
        performMove(kingMove2)
        performMove(kingMove3)
        undoMove("player")

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | startingSetup | kingMove1 | kingMove2 | kingMove3 | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 e2"   | "e2 e1"   | "e1 e2"   | "e1 c1"      || startingSetup
        "white king short" | "wk_e1 wr_h1" | "e1 e2"   | "e2 e1"   | "e1 e2"   | "e1 g1"      || startingSetup
        "black king long"  | "bk_e8 br_a8" | "e8 e7"   | "e7 e8"   | "e8 e7"   | "e8 c8"      || startingSetup
        "black king short" | "bk_e8 br_h8" | "e8 e7"   | "e7 e8"   | "e8 e7"   | "e8 g8"      || startingSetup
    }

    def "cannot castle if there are pieces between king and rook"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e1 wr_a1 wp_b1" | "e1 c1" || startingSetup
        "wk_e1 wr_a1 wp_c1" | "e1 c1" || startingSetup
        "wk_e1 wr_a1 wp_d1" | "e1 c1" || startingSetup
        "wk_e1 wr_h1 wp_f1" | "e1 g1" || startingSetup
        "wk_e1 wr_h1 wp_g1" | "e1 g1" || startingSetup
        "bk_e8 br_a8 bp_b8" | "e8 c8" || startingSetup
        "bk_e8 br_a8 bp_c8" | "e8 c8" || startingSetup
        "bk_e8 br_a8 bp_d8" | "e8 c8" || startingSetup
        "bk_e8 br_h8 bp_f8" | "e8 g8" || startingSetup
        "bk_e8 br_h8 bp_g8" | "e8 g8" || startingSetup
    }

    def "cannot castle if king is in check"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e1 wr_a1 bq_e8" | "e1 c1" || startingSetup
        "wk_e1 wr_h1 bq_e8" | "e1 g1" || startingSetup
        "bk_e8 br_a8 wq_e1" | "e8 c8" || startingSetup
        "bk_e8 br_h8 wq_e1" | "e8 g8" || startingSetup
    }

    def "can castle if rook is under attack"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e1 wr_a1 bq_a8" | "e1 c1" || "wk_c1 wr_d1 bq_a8"
    }

    def "can castle if field adjacent to the rook is under attack"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e1 wr_a1 bq_b8" | "e1 c1" || "wk_c1 wr_d1 bq_b8"
        "bk_e8 br_a8 wq_b1" | "e8 c8" || "bk_c8 br_d8 wq_b1"
    }

    def "cannot castle if king would be attack during move"() {
            given:
            setupPieces(startingSetup)

            when:
            performMove(move)

            then:
            assertPositions(positions)

            where:
            startingSetup       | move    || positions
            "wk_e1 wr_a1 bq_c8" | "e1 c1" || startingSetup
            "wk_e1 wr_a1 bq_d8" | "e1 c1" || startingSetup
            "wk_e1 wr_h1 bq_f8" | "e1 g1" || startingSetup
            "bk_e8 br_a8 wq_c1" | "e8 c8" || startingSetup
            "bk_e8 br_a8 wq_d1" | "e8 c8" || startingSetup
            "bk_e8 br_h8 wq_f1" | "e8 g8" || startingSetup
    }

    def "verifies castling infinite loop fix"() {
        given:
        setupPieces("wk_e1 bk_e8 bb_c8")

        when:
        performMove("c8 b7")

        then:
        assertPositions("wk_e1 bk_e8 bb_b7")
    }
}
