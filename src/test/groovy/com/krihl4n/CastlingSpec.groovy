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

    def "cannot do white king short castle if rook moved before"() {
        given:
        setupPieces("wk_e1 wr_h1")

        and:
        performMove("h1 h2")
        performMove("h2 h1")

        when:
        performMove("e1 g1")

        then:
        assertPositions("wk_e1 wr_h1")
    }

    def "cannot do white king long castle if rook moved before"() {
        given:
        setupPieces("wk_e1 wr_a1")

        and:
        performMove("a1 a2")
        performMove("a2 a1")

        when:
        performMove("e1 c1")

        then:
        assertPositions("wk_e1 wr_a1")
    }

    def "cannot do black king short castle if rook moved before"() {
        given:
        setupPieces("bk_e8 br_h8")

        and:
        performMove("h8 h7")
        performMove("h7 h8")

        when:
        performMove("e8 g8")

        then:
        assertPositions("bk_e8 br_h8")
    }

    def "cannot do black king long castle if rook moved before"() {
        given:
        setupPieces("bk_e8 br_a8")

        and:
        performMove("a8 a7")
        performMove("a7 a8")

        when:
        performMove("e8 c8")

        then:
        assertPositions("bk_e8 br_a8")
    }

    def "can't do castling of type #type when king moved before"() {
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
}
