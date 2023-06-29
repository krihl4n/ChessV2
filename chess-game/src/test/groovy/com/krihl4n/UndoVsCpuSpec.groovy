package com.krihl4n

import com.krihl4n.game.GameMode

class UndoVsCpuSpec extends BaseGameSpec {

    void setup() {
        setupTests(GameMode.VS_COMPUTER)
        game.initialize(GameMode.VS_COMPUTER)
        game.playerReady("player1", "white")
        game.playerReady("cpu", null)
    }

    def "should undo last move"() {
        given:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove("player1","a2 a3")

        when:
        undoMove("player1")

        then:
        assertPositions("wp_a2 bp_a7")
        game.colorAllowedToMove().toString() == "white"
    }

    def "should undo cpu and player moves if cpu performed move"() {
        given:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove("player1","a2 a3")
        performMove("cpu","a7 a6")

        when:
        undoMove("player1")

        then:
        assertPositions("wp_a2 bp_a7")
        game.colorAllowedToMove().toString() == "white"
    }

    def "undoing does nothing if there is nothing to undo" () {
        given:
        setupPieces("wp_a2 bp_a7")

        when:
        undoMove("player1")

        then:
        assertPositions("wp_a2 bp_a7")
        game.colorAllowedToMove().toString() == "white"
    }

    def "should not undo cpu first move" () {
        given:
        setupTests(GameMode.VS_COMPUTER)
        game.initialize(GameMode.VS_COMPUTER)
        game.playerReady("cpu", "white")
        game.playerReady("player1", null)

        and:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove("cpu","a2 a3")

        when:
        undoMove("player1")

        then:
        assertPositions("wp_a3 bp_a7")
        game.colorAllowedToMove().toString() == "black"
    }
}
