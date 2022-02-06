package com.krihl4n.players

import com.krihl4n.model.Color
import spock.lang.Specification

class PlayersFacadeSpec extends Specification {

    PlayersFacade playersFacade

    void setup() {
        playersFacade = new PlayersFacade()
    }

    def "should register player with selected color"() {
        when:
        playersFacade.registerPlayer("player1", Color.@WHITE)

        then:
        playersFacade.getPlayer("player1") == new Player("player1", Color.@WHITE)
    }

    def "player gets random color if not specified explicitly"() {
        when:
        playersFacade.registerPlayer("player1", null)

        then:
        playersFacade.getPlayer("player1").getId() == "player1"
        [Color.@BLACK, Color.@WHITE].contains(playersFacade.getPlayer("player1").getColor())
    }

    def "can register second player"() {
        given:
        playersFacade.registerPlayer("player1", null)

        when:
        playersFacade.registerPlayer("player2", null)

        then:
        playersFacade.getPlayer("player2").getId() == "player2"
    }

    def "second player color preferences don't matter"() {
        given:
        playersFacade.registerPlayer("player1", Color.@BLACK)

        when:
        playersFacade.registerPlayer("player2", Color.@BLACK)

        then:
        playersFacade.getPlayer("player1") == new Player("player1", Color.@BLACK)
        playersFacade.getPlayer("player2") == new Player("player2", Color.@WHITE)
    }

    def "players cannot have same ids"() {
        given:
        playersFacade.registerPlayer("player1", Color.@BLACK)

        when:
        playersFacade.registerPlayer("player1", Color.@BLACK)

        then:
        thrown(IllegalArgumentException)
    }

    def "cannot register more than 2 players"() {
        given:
        playersFacade.registerPlayer("player1", null)
        playersFacade.registerPlayer("player2", null)

        when:
        playersFacade.registerPlayer("player3", null)

        then:
        thrown(UnsupportedOperationException)
    }
}
