package com.krihl4n

import com.krihl4n.game.GameMode

class UndoVsFriendSpec extends BaseGameSpec {

    void setup() {
        setupTests(GameMode.VS_FRIEND)
        game.initialize(GameMode.VS_FRIEND)
        game.playerReady("player1", "white")
        game.playerReady("player2", null)
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

    def "should not be able to undo if opponent performed his move"() {
        given:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove("player1","a2 a3")
        performMove("player2","a7 a6")

        when:
        undoMove("player1")

        then:
        assertPositions("wp_a3 bp_a6")
        game.colorAllowedToMove().toString() == "white"
    }

    def "can undo max one move"() {
        given:
        setupPieces("wp_a2 bp_a7")

        and:
        performMove("player1","a2 a3")
        performMove("player2","a7 a6")
        performMove("player1","a3 a4")

        when:
        undoMove("player1")
        undoMove("player1")

        then:
        assertPositions("wp_a3 bp_a6")
        game.colorAllowedToMove().toString() == "white"
    }

    def "undoing does nothing if there is nothing to undo" () {
        given:
        setupPieces("wp_a2 bp_a7")

        when:
        undoMove("player2")

        then:
        assertPositions("wp_a2 bp_a7")
        game.colorAllowedToMove().toString() == "white"
    }
}
