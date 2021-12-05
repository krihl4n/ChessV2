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
}
