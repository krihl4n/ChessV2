package com.krihl4n

class GameSpec extends BaseGameSpec {

    void setup() {
        setupPieces("wp_a2")
    }

    def "can't perform move if game not started"() {
        when:
        game.performMove("a2", "a3")

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.start()

        when:
        def result = game.performMove("a2", "a3")

        then:
        result
    }

    def "can't perform move if game has been finished"() {
        given:
        game.start()
        game.finish()

        when:
        game.performMove("a2", "a3")

        then:
        thrown(IllegalStateException)
    }

    def "position tracker should be updated when move performed"() {
        given:
        game.start()

        when:
        game.performMove("a2", "a3")

        then:
        assertPositions("wp_a3")
    }

    def "should return false if move couldn't be performed"() {
        given:
        game.start()
        positionTracker.removePieceFromField(aField("a2"))

        when:
        def result = game.performMove("a2", "a3")

        then:
        !result
    }

    def "should undo move"() {
        given:
        game.start()

        when:
        game.performMove("a2", "a3")
        and:
        game.undoMove()

        then:
        assertPositions("wp_a2")
    }

    def "should redo move"() {
        given:
        game.start()

        when:
        game.performMove("a2", "a3")
        and:
        game.undoMove()
        and:
        game.redoMove()

        then:
        assertPositions("wp_a3")
    }

    def "should not be able to perform illegal moves"() {
        given:
        game.start()
        and:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a2"))

        when:
        def result = game.performMove(aField("a2"), aField("b8"))

        then:
        !result
    }
}
