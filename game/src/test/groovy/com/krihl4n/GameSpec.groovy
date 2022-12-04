package com.krihl4n

class GameSpec extends BaseGameSpec {

    void setup() {
        setupPieces("wp_a2")
    }

    def "can't perform move if game not started"() {
        when:
        game.performMove("player","a2", "a3")

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.initialize()
        game.playerReady("player", null)

        when:
        game.performMove("player","a2", "a3")

        then:
        noExceptionThrown()
    }

    def "can't perform move if game has been finished"() {
        given:
        game.initialize()
        game.playerReady("player", null)
        game.resign("player")

        when:
        game.performMove("player","a2", "a3")

        then:
        thrown(IllegalStateException)
    }

    def "position tracker should be updated when move performed"() {
        given:
        game.initialize()
        game.playerReady("player", null)

        when:
        game.performMove("player","a2", "a3")

        then:
        assertPositions("wp_a3")
    }

    def "should return false if move couldn't be performed"() {
        given:
        game.initialize()
        game.playerReady("player", null)
        positionTracker.removePieceFromField(aField("a2"))

        when:
        def result = game.performMove("player","a2", "a3")

        then:
        !result
    }

    def "should undo move"() {
        given:
        game.initialize()
        game.playerReady("player", null)

        when:
        game.performMove("player","a2", "a3")
        and:
        game.undoMove()

        then:
        assertPositions("wp_a2")
    }

    def "should redo move"() {
        given:
        game.initialize()
        game.playerReady("player", null)

        when:
        game.performMove("player","a2", "a3")
        and:
        game.undoMove()
        and:
        game.redoMove()

        then:
        assertPositions("wp_a3")
    }

    def "should not be able to perform illegal moves"() {
        given:
        game.initialize()
        game.playerReady("player", null)
        and:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a2"))

        when:
        def result = game.performMove("player","a2", "b8")

        then:
        !result
    }

    def "cannot start game if no players registered"() {

    }
}
