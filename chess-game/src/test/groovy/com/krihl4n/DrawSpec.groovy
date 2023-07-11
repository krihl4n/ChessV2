package com.krihl4n

import com.krihl4n.game.GameMode
import com.krihl4n.game.result.GameResult
import com.krihl4n.game.result.Result
import com.krihl4n.game.result.ResultReason

class DrawSpec extends BaseGameSpec {

    // https://support.chess.com/article/128-what-does-insufficient-mating-material-mean

    void setup() {
        gameCanBeFinished()
        game.initialize(GameMode.TEST_MODE)
        game.playerReady("player", null)
    }

    def "stalemate should result in a draw"() {
        given:
        setupPieces("wk_f6 wb_g7 bk_h7")

        when:
        performMove("f6 f7")

        then:
        assertPositions("wk_f7 wb_g7 bk_h7")

        and:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.STALEMATE)
    }

    def "insufficient material for #scenario"() {
        given:
        setupPieces(setup)

        when:
        performMove("a1 a2")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.INSUFFICIENT_MATERIAL)

        where:
        scenario                                | setup
        "two kings"                             | "wk_a1 bq_a2 bk_h1"
        "two kings, black bishop"               | "wk_a1 bq_a2 bk_h1 bb_h2"
        "two kings, white bishop"               | "wk_a1 bq_a2 bk_h1 wb_h2"
        "two kings, black knight"               | "wk_a1 bq_a2 bk_h1 wn_h2"
        "two kings, white knight"               | "wk_a1 bq_a2 bk_h1 wn_h2"
        "two kings, white bishop, black bishop" | "wk_a1 bq_a2 bk_h1 wb_h2 bb_h3"
        "two kings, white knight, black knight" | "wk_a1 bq_a2 bk_h1 wn_h2 bn_h3"
        "two kings, white bishop, black knight" | "wk_a1 bq_a2 bk_h1 wb_h2 bn_h3"
        "two kings, black bishop, white knight" | "wk_a1 bq_a2 bk_h1 bb_h2 wn_h3"
        "two kings, two white knights"          | "wk_a1 bq_a2 bk_h1 wn_h2 wn_h3"
        "two kings, two black knights"          | "wk_a1 bq_a2 bk_h1 bn_h2 bn_h3"
    }

    def "50 move repetition rule draw"() {
        given:
        setupPieces("wk_a1 wq_a2 bk_h8 bq_h7 wp_b2 bp_g7")

        and:
        performMoves(
                "a2 a3", "h7 h6",
                "a3 a4", "h6 h5",
                "a4 a5", "h5 h4",
                "a5 a6", "h4 h3",
                "a6 a7", "h3 h2",
                "a7 b7", "h2 g2",
                "b7 b6", "g2 g3",
                "b6 b5", "g3 g4",
                "b5 b4", "g4 g5",
                "b4 b3", "g5 g6", // 10
                "b3 c3", "g6 f6",
                "c3 c4", "f6 f5",
                "c4 c5", "f5 f4",
                "c5 c6", "f4 f3",
                "c6 c7", "f3 f2",
                "c7 d7", "f2 e2",
                "d7 d6", "e2 e3",
                "d6 d5", "e3 e4",
                "d5 d4", "e4 e5",
                "d4 d3", "e5 e6", // 20
                "d3 d2", "e6 e7",
                "d2 d3", "e7 e6",
                "d3 d4", "e6 e5",
                "d4 d5", "e5 e4",
                "d5 d6", "e4 e3",
                "d6 d7", "e3 e2",
                "d7 c7", "e2 f2",
                "c7 c6", "f2 f3",
                "c6 c5", "f3 f4",
                "c5 c4", "f4 f5", // 30
                "c4 c3", "f5 f6",
                "c3 b3", "f6 g6",
                "b3 b4", "g6 g5",
                "b4 b5", "g5 g4",
                "b5 b6", "g4 g3",
                "b6 b7", "g3 g2",
                "b7 a7", "g2 h2",
                "a7 a6", "h2 h3",
                "a6 a5", "h3 h4",
                "a5 a4", "h4 h5", // 40
                "a4 a3", "h5 h6",
                "a3 a2", "h6 h7",
                "a2 b1", "h7 g8",
                "b1 g1", "g8 b8",// up to this point queen moves. wq_a2 bq_h7
                "a1 b1", "h8 g8",
                "b1 c1", "g8 f8",
                "c1 d1", "f8 e8",
                "d1 e1", "e8 d8",
                "e1 f1", "d8 c8", // 49
                "f1 f2"
        )

        when:
        performMove("c8 c7")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.REPETITION)
    }

    def "50 move repetition rule draw with pawn resetting the counter"() {
        given:
        setupPieces("wk_a1 wq_a2 bk_h8 bq_h7 wp_b2 bp_g7")

        and:
        performMoves(
                "a2 a3", "h7 h6",
                "a3 a4", "h6 h5",
                "a4 a5", "h5 h4",
                "a5 a6", "h4 h3",
                "a6 a7", "h3 h2",
                "a7 b7", "h2 g2",
                "b7 b6", "g2 g3",
                "b6 b5", "g3 g4",
                "b5 b4", "g4 g5",
                "b4 b3", "g5 g6", // 10
                "b3 c3", "g6 f6",
                "c3 c4", "f6 f5",
                "c4 c5", "f5 f4",
                "c5 c6", "f4 f3",
                "c6 c7", "f3 f2",
                "c7 d7", "f2 e2",
                "d7 d6", "e2 e3",
                "d6 d5", "e3 e4",
                "d5 d4", "e4 e5",
                "d4 d3", "e5 e6", // 20
                "d3 d2", "e6 e7",
                "d2 d3", "e7 e6",
                "d3 d4", "e6 e5",
                "d4 d5", "e5 e4",
                "d5 d6", "e4 e3",
                "d6 d7", "e3 e2",
                "d7 c7", "e2 f2",
                "c7 c6", "f2 f3",
                "c6 c5", "f3 f4",
                "c5 c4", "f4 f5", // 30
                "c4 c3", "f5 f6",
                "c3 b3", "f6 g6",
                "b3 b4", "g6 g5",
                "b4 b5", "g5 g4",
                "b5 b6", "g4 g3",
                "b6 b7", "g3 g2",
                "b7 a7", "g2 h2",
                "a7 a6", "h2 h3",
                "a6 a5", "h3 h4",
                "a5 a4", "h4 h5", // 40
                "a4 a3", "h5 h6",
                "a3 a2", "h6 h7",
                "a2 b1", "h7 g8",
                "b1 g1", "g8 b8",// up to this point queen moves. wq_a2 bq_h7
                "a1 b1", "h8 g8",
                "b1 c1", "g8 f8",
                "c1 d1", "f8 e8",
                "d1 e1", "e8 d8",
                "e1 f1", "d8 c8", // 49
                "f1 f2"
        )

        and: // pawn moves
        performMove("b2 b3")

        when:
        performMove("c8 c7")

        then:
        !game.isGameFinished()
    }

    def "50 move repetition rule draw with attack resetting the counter"() {
        given:
        setupPieces("wk_a1 wq_a2 bk_h8 bq_h7 wp_b2 bp_g7")

        and:
        performMoves(
                "a2 a3", "h7 h6",
                "a3 a4", "h6 h5",
                "a4 a5", "h5 h4",
                "a5 a6", "h4 h3",
                "a6 a7", "h3 h2",
                "a7 b7", "h2 g2",
                "b7 b6", "g2 g3",
                "b6 b5", "g3 g4",
                "b5 b4", "g4 g5",
                "b4 b3", "g5 g6", // 10
                "b3 c3", "g6 f6",
                "c3 c4", "f6 f5",
                "c4 c5", "f5 f4",
                "c5 c6", "f4 f3",
                "c6 c7", "f3 f2",
                "c7 d7", "f2 e2",
                "d7 d6", "e2 e3",
                "d6 d5", "e3 e4",
                "d5 d4", "e4 e5",
                "d4 d3", "e5 e6", // 20
                "d3 d2", "e6 e7",
                "d2 d3", "e7 e6",
                "d3 d4", "e6 e5",
                "d4 d5", "e5 e4",
                "d5 d6", "e4 e3",
                "d6 d7", "e3 e2",
                "d7 c7", "e2 f2",
                "c7 c6", "f2 f3",
                "c6 c5", "f3 f4",
                "c5 c4", "f4 f5", // 30
                "c4 c3", "f5 f6",
                "c3 b3", "f6 g6",
                "b3 b4", "g6 g5",
                "b4 b5", "g5 g4",
                "b5 b6", "g4 g3",
                "b6 b7", "g3 g2",
                "b7 a7", "g2 h2",
                "a7 a6", "h2 h3",
                "a6 a5", "h3 h4",
                "a5 a4", "h4 h5", // 40
                "a4 a3", "h5 h6",
                "a3 a2", "h6 h7",
                "a2 b1", "h7 g8",
                "b1 g1", "g8 b8",// up to this point queen moves. wq_a2 bq_h7
                "a1 b1", "h8 g8",
                "b1 c1", "g8 f8",
                "c1 d1", "f8 e8",
                "d1 e1", "e8 d8",
                "e1 f1", "d8 c8", // 49
                "f1 f2"
        )

        and: // attack
        performMove("b8 b2")

        when:
        performMove("c8 c7")

        then:
        !game.isGameFinished()
    }

    def "check mate has precedence over 50 move repetition"() {
        given:
        setupPieces("wk_a1 wp_a2 wp_b2 wq_c1 bk_a8 bp_a7 bp_b7 bq_a6")

        and:
        performMoves(
                "c1 d1", "a6 a5",
                "d1 e1", "a5 a6",
                "e1 f1", "a6 a5",
                "f1 g1", "a5 a6",
                "g1 h1", "a6 a5",
                "h1 h2", "a5 a6",
                "h2 g2", "a6 a5",
                "g2 f2", "a5 a6",
                "f2 e2", "a6 a5",
                "e2 d2", "a5 a6", // 10
                "d2 c2", "a6 a5",
                "c2 c3", "a5 a6",
                "c3 d3", "a6 a5",
                "d3 e3", "a5 a6",
                "e3 f3", "a6 a5",
                "f3 g3", "a5 a6",
                "g3 h3", "a6 a5",
                "h3 h4", "a5 a6",
                "h4 g4", "a6 a5",
                "g4 f4", "a5 a6", // 20
                "f4 e4", "a6 a5",
                "e4 d4", "a5 a6",
                "d4 c4", "a6 a5",
                "c4 c5", "a5 a6",
                "c5 d5", "a6 a5",
                "d5 e5", "a5 a6",
                "e5 f5", "a6 a5",
                "f5 g5", "a5 a6",
                "g5 h5", "a6 a5",
                "h5 h6", "a5 a6", // 30
                "h6 g6", "a6 a5",
                "g6 f6", "a5 a6",
                "f6 e6", "a6 a5",
                "e6 d6", "a5 a6",
                "d6 c6", "a6 a5",
                "c6 c7", "a5 a6",
                "c7 d7", "a6 a5",
                "d7 e7", "a5 a6",
                "e7 f7", "a6 a5",
                "f7 g7", "a5 a6", // 40
                "g7 h7", "a6 a5",
                "h7 h6", "a5 a6",
                "h6 g6", "a6 a5",
                "g6 f6", "a5 a6",
                "f6 e6", "a6 a5",
                "e6 d6", "a5 a6",
                "d6 c6", "a6 a5",
                "c6 c5", "a5 a6",
                "c5 d5", "a6 c6",
                "d5 e5",  // 50
        )

        when: // 50th move, but check mate
        performMove("c6 c1")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.BLACK_PLAYER_WON, ResultReason.CHECK_MATE)
    }

    def "50 move repetition rule draw with undo"() {
        given:
        setupPieces("wk_a1 wq_a2 bk_h8 bq_h7 wp_b2 bp_g7")

        and:
        performMoves(
                "a2 a3", "h7 h6",
                "a3 a4", "h6 h5",
                "a4 a5", "h5 h4",
                "a5 a6", "h4 h3",
                "a6 a7", "h3 h2",
                "a7 b7", "h2 g2",
                "b7 b6", "g2 g3",
                "b6 b5", "g3 g4",
                "b5 b4", "g4 g5",
                "b4 b3", "g5 g6", // 10
                "b3 c3", "g6 f6",
                "c3 c4", "f6 f5",
                "c4 c5", "f5 f4",
                "c5 c6", "f4 f3",
                "c6 c7", "f3 f2",
                "c7 d7", "f2 e2",
                "d7 d6", "e2 e3",
                "d6 d5", "e3 e4",
                "d5 d4", "e4 e5",
                "d4 d3", "e5 e6", // 20
                "d3 d2", "e6 e7",
                "d2 d3", "e7 e6",
                "d3 d4", "e6 e5",
                "d4 d5", "e5 e4",
                "d5 d6", "e4 e3",
                "d6 d7", "e3 e2",
                "d7 c7", "e2 f2",
                "c7 c6", "f2 f3",
                "c6 c5", "f3 f4",
                "c5 c4", "f4 f5", // 30
                "c4 c3", "f5 f6",
                "c3 b3", "f6 g6",
                "b3 b4", "g6 g5",
                "b4 b5", "g5 g4",
                "b5 b6", "g4 g3",
                "b6 b7", "g3 g2",
                "b7 a7", "g2 h2",
                "a7 a6", "h2 h3",
                "a6 a5", "h3 h4",
                "a5 a4", "h4 h5", // 40
                "a4 a3", "h5 h6",
                "a3 a2", "h6 h7",
                "a2 b1", "h7 g8",
                "b1 g1", "g8 b8",// up to this point queen moves. wq_a2 bq_h7
                "a1 b1", "h8 g8",
                "b1 c1", "g8 f8",
                "c1 d1", "f8 e8",
                "d1 e1", "e8 d8",
                "e1 f1", "d8 c8", // 49
                "f1 f2"
        )

        and:
        undoMove("player")

        when:
        performMove("f1 f2")
        performMove("c8 c7")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.REPETITION)
    }

    def "50 move repetition rule draw with pawn undo"() {
        given:
        setupPieces("wk_a1 wq_a2 bk_h8 bq_h7 wp_b2 bp_g7")

        and:
        performMoves(
                "a2 a3", "h7 h6",
                "a3 a4", "h6 h5",
                "a4 a5", "h5 h4",
                "a5 a6", "h4 h3",
                "a6 a7", "h3 h2",
                "a7 b7", "h2 g2",
                "b7 b6", "g2 g3",
                "b6 b5", "g3 g4",
                "b5 b4", "g4 g5",
                "b4 b3", "g5 g6", // 10
                "b3 c3", "g6 f6",
                "c3 c4", "f6 f5",
                "c4 c5", "f5 f4",
                "c5 c6", "f4 f3",
                "c6 c7", "f3 f2",
                "c7 d7", "f2 e2",
                "d7 d6", "e2 e3",
                "d6 d5", "e3 e4",
                "d5 d4", "e4 e5",
                "d4 d3", "e5 e6", // 20
                "d3 d2", "e6 e7",
                "d2 d3", "e7 e6",
                "d3 d4", "e6 e5",
                "d4 d5", "e5 e4",
                "d5 d6", "e4 e3",
                "d6 d7", "e3 e2",
                "d7 c7", "e2 f2",
                "c7 c6", "f2 f3",
                "c6 c5", "f3 f4",
                "c5 c4", "f4 f5", // 30
                "c4 c3", "f5 f6",
                "c3 b3", "f6 g6",
                "b3 b4", "g6 g5",
                "b4 b5", "g5 g4",
                "b5 b6", "g4 g3",
                "b6 b7", "g3 g2",
                "b7 a7", "g2 h2",
                "a7 a6", "h2 h3",
                "a6 a5", "h3 h4",
                "a5 a4", "h4 h5", // 40
                "a4 a3", "h5 h6",
                "a3 a2", "h6 h7",
                "a2 b1", "h7 g8",
                "b1 g1", "g8 b8",// up to this point queen moves. wq_a2 bq_h7
                "a1 b1", "h8 g8",
                "b1 c1", "g8 f8",
                "c1 d1", "f8 e8",
                "d1 e1", "e8 d8",
                "e1 f1", "d8 c8", // 49
                "b2 b3"
        )

        and:
        undoMove("player")

        when:
        performMove("f1 f2")
        performMove("c8 c7")

        then:
        game.isGameFinished()
        game.getResult() == new GameResult(Result.DRAW, ResultReason.REPETITION)
    }
}
