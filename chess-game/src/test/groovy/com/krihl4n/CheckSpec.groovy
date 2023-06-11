package com.krihl4n

import com.krihl4n.game.GameMode
import com.krihl4n.model.Color
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class CheckSpec extends BaseGameSpec {

    void setup() {
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "king should escape from check"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup | move    || positions
        "wk_e4 bq_e8" | "e4 e5" || startingSetup
        "wk_e4 bq_e8" | "e4 e3" || startingSetup
        "wk_e4 bq_e8" | "e4 f4" || "wk_f4 bq_e8"
        "wk_e4 bq_e8" | "e4 f3" || "wk_f3 bq_e8"
        "wk_e4 bq_e8" | "e4 f5" || "wk_f5 bq_e8"
        "wk_e4 bq_e8" | "e4 d4" || "wk_d4 bq_e8"
        "wk_e4 bq_e8" | "e4 d5" || "wk_d5 bq_e8"
        "wk_e4 bq_e8" | "e4 d3" || "wk_d3 bq_e8"
    }

    def "cannot move another piece leaving king checked"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e4 wq_a1 bq_e8" | "a1 a2" || startingSetup
    }

    def "cannot cause own king to be checked after other piece move"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e4 wq_e5 bq_e8" | "e5 f5" || startingSetup
    }

    def "it is safe to move other pieces until king is not attacked"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e4 wq_e5 bq_e8" | "e5 e6" || "wk_e4 wq_e6 bq_e8"
    }

    def "attack on the king can be blocked"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e4 wq_f5 bq_e8" | "f5 e5" || "wk_e4 wq_e5 bq_e8"
    }

    def "undone move can end up with king being checked"() {
        given:
        setupPieces(startingSetup)

        when:
        performMove(move)

        and:
        undoMove()

        then:
        assertPositions(positions)

        where:
        startingSetup       | move    || positions
        "wk_e4 wq_f5 bq_e8" | "f5 e5" || "wk_e4 wq_f5 bq_e8"
    }

    def "check test with full piece setup" () {
        given:
        positionTracker.resetInitialGameSetup(null)

        when:
        performMove("a2 a3")

        then:
        assertSinglePosition(new Piece(Color.@WHITE, Type.PAWN), "a3")
    }
}
