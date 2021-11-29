package com.krihl4n

class CastlingSpec extends BaseGameSpec {

    void setup() {
        game.start()
    }

    def "castling white king long"() {
        given:
        setupPieces("wk_e1 wr_a1")

        when:
        game.performMove("e1", "c1")

        then:
        assertPositions("wk_c1 wr_d1")
    }

    def "castling white king short"() {
        // king e1 g1
        // rook h1 f1
    }

    def "castling black king long"() {
        // king e8 c8
        // rook a8 d8
    }

    def "castling black king short"() {
        // king e8 g8
        // rook h8 f8
    }
}
