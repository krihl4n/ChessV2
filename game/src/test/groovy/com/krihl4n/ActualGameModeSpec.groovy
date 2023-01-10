package com.krihl4n

import com.krihl4n.api.dto.GameModeDto

class ActualGameModeSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameModeDto.VS_FRIEND)
        game.playerReady("WHITE")
        game.playerReady("BLACK")
    }

    def "black pieces cannot start the game"() {
        given:
        setupPieces("wp_d2 bp_d7")

        when:
        performMove(game.fetchPlayerOne().id, "d7 d6")

        then:
        assertPositions("wp_d2 bp_d7")
    }

    def "black pieces can move after white pieces"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove(game.fetchPlayerOne().id, "d2 d3")

        when:
        performMove(game.fetchPlayerTwo().id, "d7 d6")

        then:
        assertPositions("wp_d3 bp_d6")
    }

    def "white pieces cannot move when it's black's turn"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove(game.fetchPlayerOne().id, "d2 d3")

        when:
        performMove(game.fetchPlayerOne().id, "d3 d4")

        then:
        assertPositions("wp_d3 bp_d7")
    }

    def "game mode handles the undo action"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove(game.fetchPlayerOne().id, "d2 d3")
        performMove(game.fetchPlayerTwo().id, "d7 d6")

        when:
        game.undoMove()
        performMove(game.fetchPlayerTwo().id, "d7 d5")

        then:
        assertPositions("wp_d3 bp_d5")
    }

    def "white pieces start again after undo moves"() {
        given:
        setupPieces("wp_d2 bp_d7")

        and:
        performMove(game.fetchPlayerOne().id, "d2 d3")
        game.undoMove()

        when:
        performMove(game.fetchPlayerOne().id, "d2 d3")

        then:
        assertPositions("wp_d3 bp_d7")
    }

    def "white player shouldn't be able to move black pieces"() {
        given:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove(game.fetchPlayerOne().id,"a2 a3")
        performMove(game.fetchPlayerTwo().id,"a7 a6")

        when:
        performMove(game.fetchPlayerOne().id, "a6 a5")

        then:
        assertPositions("wp_a3 bp_a6")
    }
}
