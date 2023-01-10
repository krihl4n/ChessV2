package com.krihl4n.players

import com.krihl4n.model.Color
import spock.lang.Specification

class ActualGamePlayersManagerSpec extends Specification {

    ActualGamePlayersManager playersFacade

    void setup() {
        playersFacade = new ActualGamePlayersManager()
    }

    def "should register player with selected color"() {
        when:
        playersFacade.registerPlayer(Color.@WHITE)

        then:
        playersFacade.getPlayerOne().color == Color.@WHITE
    }

    def "player gets random color if not specified explicitly"() {
        when:
        playersFacade.registerPlayer(null)

        then:
        [Color.@BLACK, Color.@WHITE].contains(playersFacade.getPlayerOne().getColor())
    }

    def "can register second player"() {
        given:
        playersFacade.registerPlayer(null)

        when:
        playersFacade.registerPlayer(null)

        then:
        playersFacade.getPlayerTwo() != null
    }

    def "second player color preferences don't matter"() {
        given:
        playersFacade.registerPlayer(Color.@BLACK)

        when:
        playersFacade.registerPlayer(Color.@BLACK)

        then:
        playersFacade.getPlayerOne().color == Color.@BLACK
        playersFacade.getPlayerTwo().color == Color.@WHITE
    }

    def "cannot register more than 2 players"() {
        given:
        playersFacade.registerPlayer(null)
        playersFacade.registerPlayer(null)

        when:
        playersFacade.registerPlayer(null)

        then:
        thrown(UnsupportedOperationException)
    }

    def "should return true if player registration is complete"() {
        when:
        def isComplete1 = playersFacade.registerPlayer(null)
        def isComplete2 = playersFacade.registerPlayer(null)

        then:
        !isComplete1
        isComplete2
    }
}
