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
        playersFacade.registerPlayer("player", Color.@WHITE)

        then:
        playersFacade.getPlayerOne().color == Color.@WHITE
    }

    def "player gets random color if not specified explicitly"() {
        when:
        playersFacade.registerPlayer("player1", null)

        then:
        [Color.@BLACK, Color.@WHITE].contains(playersFacade.getPlayerOne().getColor())
    }

    def "can register second player"() {
        given:
        playersFacade.registerPlayer("player1", null)

        when:
        playersFacade.registerPlayer("player2", null)

        then:
        playersFacade.getPlayerTwo() != null
    }

    def "second player color preferences don't matter"() {
        given:
        playersFacade.registerPlayer("player1", Color.@BLACK)

        when:
        playersFacade.registerPlayer("player2", Color.@BLACK)

        then:
        playersFacade.getPlayerOne().color == Color.@BLACK
        playersFacade.getPlayerTwo().color == Color.@WHITE
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

    def "should return true if player registration is complete"() {
        when:
        def isComplete1 = playersFacade.registerPlayer("player1", null)
        def isComplete2 = playersFacade.registerPlayer("player2", null)

        then:
        !isComplete1
        isComplete2
    }
}
