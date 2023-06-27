package com.krihl4n

import com.krihl4n.game.GameMode

class GameSpec extends BaseGameSpec {

    void setup() {
        setupPieces("wp_a2")
    }

    def "can't perform move if game not started"() {
        when:
        performMove("player", "a2 a3")

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)

        when:
        performMove("player", "a2 a3")

        then:
        noExceptionThrown()
    }

    def "can't perform move if game has been finished"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
        game.resign("player")

        when:
        performMove("player", "a2 a3")

        then:
        thrown(IllegalStateException)
    }

    def "position tracker should be updated when move performed"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)

        when:
        performMove("player", "a2 a3")

        then:
        assertPositions("wp_a3")
    }

    def "should return false if move couldn't be performed"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
        positionTracker.removePieceFromField(aField("a2"))

        when:
        def result = performMove("player", "a2 a3")

        then:
        !result
    }

    def "should undo move"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)

        when:
        performMove("player", "a2 a3")

        and:
        game.undoMove()

        then:
        assertPositions("wp_a2")
    }

    def "should not be able to perform illegal moves"() {
        given:
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
        and:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a2"))

        when:
        def result = performMove("player", "a2 b7")

        then:
        !result
    }

    def "cannot start game if no players registered"() {

    }
}
