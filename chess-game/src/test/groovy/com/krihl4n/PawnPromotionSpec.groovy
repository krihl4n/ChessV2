package com.krihl4n

import com.krihl4n.game.GameMode

class PawnPromotionSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "should have info about pawn promotion when reaches the last rank"() {
        given:
        setupPieces(setup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        setup   | move    || positions
        "wp_a7" | "a7 a8" || "wp_a7"
        "bp_a2" | "a2 a1" || "bp_a2"
    }

    def "when pawn reaches the end it promotes to a queen"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithPawnPromotion(move, promotion)

        then:
        assertPositions(positions)

        where:
        setup   | move    | promotion || positions
        "wp_a7" | "a7 a8" | "queen"    || "wq_a8"
        "bp_a2" | "a2 a1" | "queen"    || "bq_a1"
        "wp_a7" | "a7 a8" | "knight"   || "wn_a8"
        "bp_a2" | "a2 a1" | "knight"   || "bn_a1"
        "wp_a7" | "a7 a8" | "rook"     || "wr_a8"
        "bp_a2" | "a2 a1" | "rook"     || "br_a1"
        "wp_a7" | "a7 a8" | "bishop"   || "wb_a8"
        "bp_a2" | "a2 a1" | "bishop"   || "bb_a1"
    }

    def "undo pawn reaches the end move"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithPawnPromotion(move, "queen")

        and:
        undoMove()

        then:
        assertPositions(positions)

        where:
        setup   | move    || positions
        "wp_a7" | "a7 a8" || setup
        "bp_a2" | "a2 a1" || setup
    }

    def "pawn reaches last rank with attack move"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithPawnPromotion(move, "knight")

        then:
        assertPositions(positions)

        where:
        setup         | move    || positions
        "wp_a7 br_b8" | "a7 b8" || "wn_b8"
        "bp_a2 wr_b1" | "a2 b1" || "bn_b1"
    }

    def "undo pawn reaches last rank with attack move"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithPawnPromotion(move, "bishop")

        and:
        undoMove()

        then:
        assertPositions(positions)

        where:
        setup         | move    || positions
        "wp_a7 br_b8" | "a7 b8" || setup
        "bp_a2 wr_b1" | "a2 b1" || setup
    }
}
