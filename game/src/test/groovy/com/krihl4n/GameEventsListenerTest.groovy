package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import spock.lang.Specification

class GameEventsListenerTest extends Specification{

    GameEventListener listener = Mock(GameEventListener)
    GameOfChess gameOfChess

    GameEventListener secondListener = Mock(GameEventListener)
    GameOfChess secondGameOfChess

    String GAME_ID = "game-id"
    String SECOND_GAME_ID = "second-game-id"

    void setup() {
        gameOfChess = new GameOfChess(GAME_ID)
        gameOfChess.registerGameEventListener(listener)

        secondGameOfChess = new GameOfChess(SECOND_GAME_ID)
        secondGameOfChess.registerGameEventListener(secondListener)
    }

    def "should notify about basic move" (){
        given:
            gameOfChess.setupChessboard()
            gameOfChess.start()

        when:
            gameOfChess.move("a2", "a3")

        then:
            1 * listener.pieceMoved(GAME_ID, "a2", "a3")
    }

    def "having two games, only one is notified about moving piece"() {
        given:
            gameOfChess.setupChessboard()
            gameOfChess.start()
        and:
            secondGameOfChess.setupChessboard()
            secondGameOfChess.start()

        when:
            secondGameOfChess.move("a2", "a3")

        then:
            1 * secondListener.pieceMoved(SECOND_GAME_ID, "a2", "a3")
            0 * listener.pieceMoved(GAME_ID, "a2", "a3")
    }
}
