package com.krihl4n

import spock.lang.Specification
import spock.lang.Subject

class GameSpecification extends Specification {

    @Subject
    def game
    def positionTracker

    def piece = new Piece(Color.WHITE, Type.PAWN)
    def from  = new Field("a2")
    def to = new Field("a3")
    
    void setup() {
        positionTracker = new PositionTracker()
        positionTracker.setPieceAtField(piece, from)
        game = new Game(positionTracker)
    }

    def "can't perform move if game not started"() {
        when:
        game.performMove(from, to)

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.start()

        when:
        def result = game.performMove(from, to)

        then:
        result == true
    }

    def "can't perform move if game has been finished"() {
        given:
        game.start()
        game.finish()

        when:
        game.performMove(from, to)

        then:
        thrown(IllegalStateException)
    }

    def "position tracker should be updated when move performed"() {
        given:
        game.start()

        when:
        game.performMove(from, to)

        then:
        positionTracker.getPieceAt(to) == piece
    }

    def "should return false if move couldn't be performed" () {
        given:
        game.start()
        positionTracker.removePieceFromField(from)

        when:
        def result = game.performMove(from, to)

        then:
        result == false
    }
}
