package com.krihl4n

import com.krihl4n.game.GameMode

class ActualGameModeSpec extends BaseGameSpec {

    void setup() {
        game.start(GameMode.ACTUAL_GAME)
        game.registerPlayer("player1", "WHITE")
        game.registerPlayer("player2", "BLACK")
    }

    def "black pieces cannot start the game"() {
        given:
        setupPieces("wp_d2 bp_d7")

        when:
        performMove("player2", "d7 d6")

        then:
        assertPositions("wp_d2 bp_d7")
    }

    def "black pieces can move after white pieces"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove("player1", "d2 d3")

        when:
        performMove("player2", "d7 d6")

        then:
        assertPositions("wp_d3 bp_d6")
    }

    def "white pieces cannot move when it's black's turn"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove("player1", "d2 d3")

        when:
        performMove("player1", "d3 d4")

        then:
        assertPositions("wp_d3 bp_d7")
    }

    def "game mode handles the undo action"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove("player1", "d2 d3")
        performMove("player2", "d7 d6")

        when:
        game.undoMove()
        performMove("player2", "d7 d5")

        then:
        assertPositions("wp_d3 bp_d5")
    }

    def "white pieces start again after undo moves"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove("player1", "d2 d3")
        game.undoMove()

        when:
        performMove("player1", "d2 d3")

        then:
        assertPositions("wp_d3 bp_d7")
    }
}
