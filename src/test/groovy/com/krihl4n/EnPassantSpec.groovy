package com.krihl4n

class EnPassantSpec extends BaseGameSpec {

    void setup() {
        game.start()
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

        and:
        performMove("e5 f6")

        then:
        assertPositions("wp_f6")
    }
}
