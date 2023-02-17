package com.krihl4n

class PawnConversionSpec extends BaseGameSpec {

    void setup() {
        game.initialize()
        game.playerReady("player", null)
    }

    def "should have info about pawn conversion when reaches the last rank"() {
        given:
        setupPieces(setup)

        when:
        performMove(move)

        then:
        thrown(IllegalArgumentException)

        where:
        setup   | move    || positions
        "wp_a7" | "a7 a8" || "wp_a7"
        "bp_a2" | "a2 a1" || "bp_a2"
    }

    def "when pawn reaches the end it converts to a queen"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithConversion(move, conversion)

        then:
        assertPositions(positions)

        where:
        setup   | move    | conversion  || positions
        "wp_a7" | "a7 a8" | "queen"     || "wq_a8"
        "bp_a2" | "a2 a1" | "queen"     || "bq_a1"
    }

    def "undo pawn reaches the end move"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithConversion(move, "queen")

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
        performMoveWithConversion(move, "queen")

        then:
        assertPositions(positions)

        where:
        setup         | move    || positions
        "wp_a7 br_b8" | "a7 b8" || "wq_b8"
        "bp_a2 wr_b1" | "a2 b1" || "bq_b1"
    }

    def "undo pawn reaches last rank with attack move"() {
        given:
        setupPieces(setup)

        when:
        performMoveWithConversion(move, "queen")

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
