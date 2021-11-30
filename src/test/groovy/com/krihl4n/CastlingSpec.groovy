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
}
