package com.krihl4n

class CastlingSpec extends BaseGameSpec {

    void setup() {
        game.start()
    }

    def "basic castling #type"() {
        given:
        setupPieces(setup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        type               | setup         | move    || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 c1" || "wk_c1 wr_d1"
        "white king short" | "wk_e1 wr_h1" | "e1 g1" || "wk_g1 wr_f1"
        "black king long"  | "bk_e8 br_a8" | "e8 c8" || "bk_c8 br_d8"
        "black king short" | "bk_e8 br_h8" | "e8 g8" || "bk_g8 br_f8"
    }

    def "basic castling #type - undo move"() {
        given:
        setupPieces(setup)

        when:
        performMove(move)
        undoMove()

        then:
        assertPositions(positions)

        where:
        type               | setup         | move    || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 c1" || "wk_e1 wr_a1"
        "white king short" | "wk_e1 wr_h1" | "e1 g1" || "wk_e1 wr_h1"
        "black king long"  | "bk_e8 br_a8" | "e8 c8" || "bk_e8 br_a8"
        "black king short" | "bk_e8 br_h8" | "e8 g8" || "bk_e8 br_h8"
    }

    def "can't do castling of type #type if rook moved before"() {
        given:
        setupPieces(setup)

        and:
        performMove(rookMove1)
        performMove(rookMove2)

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | setup         | rookMove1 | rookMove2 | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "a1 a2"   | "a2 a1"   | "e1 c1"      || setup
        "white king short" | "wk_e1 wr_h1" | "h1 h2"   | "h2 h1"   | "e1 g1"      || setup
        "black king long"  | "bk_e8 br_a8" | "a8 a7"   | "a7 a8"   | "e8 c8"      || setup
        "black king short" | "bk_e8 br_h8" | "h8 h7"   | "h7 h8"   | "e8 g8"      || setup
    }

    def "can't do castling of type #type if king moved before"() {
        given:
        setupPieces(setup)

        and:
        performMove(kingMove1)
        performMove(kingMove2)

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | setup         | kingMove1 | kingMove2 | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 e2"   | "e2 e1"   | "e1 c1"      || setup
        "white king short" | "wk_e1 wr_h1" | "e1 e2"   | "e2 e1"   | "e1 g1"      || setup
        "black king long"  | "bk_e8 br_a8" | "e8 e7"   | "e7 e8"   | "e8 c8"      || setup
        "black king short" | "bk_e8 br_h8" | "e8 e7"   | "e7 e8"   | "e8 g8"      || setup
    }

    def "can do castling of type #type after undoing king move"() {
        given:
        setupPieces(setup)

        and:
        performMove(kingMove)
        undoMove()

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | setup         | kingMove | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "e1 e2"  | "e1 c1"      || "wk_c1 wr_d1"
        "white king short" | "wk_e1 wr_h1" | "e1 e2"  | "e1 g1"      || "wk_g1 wr_f1"
        "black king long"  | "bk_e8 br_a8" | "e8 e7"  | "e8 c8"      || "bk_c8 br_d8"
        "black king short" | "bk_e8 br_h8" | "e8 e7"  | "e8 g8"      || "bk_g8 br_f8"
    }

    def "undoing king move should not allow castling again if rook moved"() {
        given:
        setupPieces(setup)

        and:
        performMove(rookMove1)
        performMove(rookMove2)
        performMove(kingMove)
        undoMove()

        when:
        performMove(castlingMove)

        then:
        assertPositions(positions)

        where:
        type               | setup         | rookMove1 | rookMove2 | kingMove | castlingMove || positions
        "white king long"  | "wk_e1 wr_a1" | "a1 a2"   | "a2 a1"   | "e1 e2"  | "e1 c1"      || "wk_e1 wr_a1"
        "white king short" | "wk_e1 wr_h1" | "h1 h2"   | "h2 h1"   | "e1 e2"  | "e1 g1"      || "wk_e1 wr_h1"
        "black king long"  | "bk_e8 br_a8" | "a8 a7"   | "a7 a8"   | "e8 e7"  | "e8 c8"      || "bk_e8 br_a8"
        "black king short" | "bk_e8 br_h8" | "h8 h7"   | "h7 h8"   | "e8 e7"  | "e8 g8"      || "bk_e8 br_h8"
    }
}
