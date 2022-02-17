package com.krihl4n

class GameEndingMovesSpec extends BaseGameSpec {

    void setup() {
        game.start()
        game.registerPlayer("player", null)
    }

    def "game should be finished if check mate"() {
        given:
        setupPieces("bk_a8 wr_h7 wr_g7")

        when:
        performMove("g7 g8")

        then:
        game.isGameFinished()
    }

    def "game is not finished if it is check but not mate"() {
        given:
            setupPieces("bk_a8 wr_h7")

        when:
            performMove("h7 h8")

        then:
            assertPositions("bk_a8 wr_h8")

        and:
            !game.isGameFinished()
    }
}
