package com.krihl4n

class CapturesSpec extends BaseGameSpec {

    void setup() {
        game.start()
    }

    def "should perform attack"() {
        given:
        setupPieces("wr_c3 bp_c5")

        when:
        game.performMove("c3", "c5")

        then:
        assertPositions("wr_c5")
    }

    def "should not attack friendly pieces"() {
        given:
        setupPieces("wr_c3 wp_c5")

        when:
        game.performMove("c3", "c5")

        then:
        assertPositions("wr_c3 wp_c5")
    }

    def "should undo attack"() {
        given:
        setupPieces("wr_c3 bp_c5")

        and:
        game.performMove("c3", "c5")

        when:
        game.undoMove()

        then:
        assertPositions("wr_c3 bp_c5")
    }
}
