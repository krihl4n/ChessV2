package com.krihl4n

import spock.lang.Specification

class GameTest extends Specification {

    def game
    def piece = new Piece(Color.WHITE, Type.PAWN)
    def from  = new Field("a2")
    def to = new Field("a3")
    
    void setup() {
        game = new Game()
    }

    def "can't perform move if game not started"() {
        when:
        game.move(piece, from, to)

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.start()

        when:
        def result = game.move(piece, from, to)

        then:
        result == true
    }

    def "can't perform move if game has been finished"() {
        given:
        game.start()
        game.finish()

        when:
        game.move(piece, from, to)

        then:
        thrown(IllegalStateException)
    }
}
