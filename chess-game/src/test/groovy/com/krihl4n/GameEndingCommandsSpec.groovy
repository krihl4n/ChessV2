package com.krihl4n

import com.krihl4n.game.GameMode
import com.krihl4n.game.result.GameResult

import static com.krihl4n.game.result.Result.BLACK_PLAYER_WON
import static com.krihl4n.game.result.Result.WHITE_PLAYER_WON
import static com.krihl4n.game.result.ResultReason.PLAYER_RESIGNED

class GameEndingCommandsSpec extends BaseGameSpec {

    void setup() {
        gameCanBeFinished()
        game.initialize(GameMode.TEST_MODE)
    }

    def "game resigned by white player"() {
        given:
        game.playerReady("player", "white")

        when:
        game.resign(game.fetchPlayerOne().id)

        then:
        game.result == new GameResult(BLACK_PLAYER_WON, PLAYER_RESIGNED)
    }

    def "game resigned by black player"() {
        given:
        game.playerReady("player", "black")

        when:
        game.resign(game.fetchPlayerOne().id)

        then:
        game.result == new GameResult(WHITE_PLAYER_WON, PLAYER_RESIGNED)
    }
}
