package com.krihl4n

import spock.lang.Specification

class GameTest extends Specification {

    def game

    void setup() {
        game = new Game()
    }

    def "can't perform move if game not started"() {
        when:
        game.move()

        then:
        thrown(IllegalStateException)
    }

    def "can perform move when game is started"() {
        given:
        game.start()

        when:
        def result = game.move()

        then:
        result == true
    }

    def "can't perform move if game has been finished"() {
        given:
        game.start()
        game.finish()

        when:
        game.move()

        then:
        thrown(IllegalStateException)
    }

//    def "when piece moved to a position, it can later be retrieved by the position"() {
//        given:
//
//
//    }
}
