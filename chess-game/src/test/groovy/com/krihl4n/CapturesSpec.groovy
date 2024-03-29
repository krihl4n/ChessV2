package com.krihl4n

import com.krihl4n.game.GameMode

class CapturesSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "should perform attack"() {
        given:
        setupPieces("wr_c3 bp_c5")

        when:
        performMove("player","c3 c5")

        then:
        assertPositions("wr_c5")
    }

    def "should not attack friendly pieces"() {
        given:
        setupPieces("wr_c3 wp_c5")

        when:
        performMove("player","c3 c5")

        then:
        assertPositions("wr_c3 wp_c5")
    }

    def "should undo attack"() {
        given:
        setupPieces("wr_c3 bp_c5")

        and:
        performMove("player","c3 c5")

        when:
        game.undoMove("player")

        then:
        assertPositions("wr_c3 bp_c5")
    }
}
