package com.krihl4n

class CheckSpec extends BaseGameSpec {

    void setup() {
        game.start()
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
}
