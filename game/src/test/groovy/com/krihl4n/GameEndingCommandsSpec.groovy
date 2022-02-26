package com.krihl4n

import com.krihl4n.game.GameResult

import static com.krihl4n.game.Result.BLACK_PLAYER_WON
import static com.krihl4n.game.Result.WHITE_PLAYER_WON
import static com.krihl4n.game.ResultReason.PLAYER_RESIGNED

class GameEndingCommandsSpec extends BaseGameSpec {

    void setup() {
        gameCanBeFinished()
        game.start()
    }

    def "game resigned by white player"() {
        given:
        game.registerPlayer("player", "white")

        when:
        game.resign("player")

        then:
        game.result == new GameResult(BLACK_PLAYER_WON, PLAYER_RESIGNED)
    }

    def "game resigned by black player"() {
        given:
        game.registerPlayer("player", "black")

        when:
        game.resign("player")

        then:
        game.result == new GameResult(WHITE_PLAYER_WON, PLAYER_RESIGNED)
    }
}
