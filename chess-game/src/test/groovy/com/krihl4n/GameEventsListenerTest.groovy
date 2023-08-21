package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.dto.PerformedMoveDto
import com.krihl4n.api.dto.PieceCaptureDto
import com.krihl4n.api.dto.PieceDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.pieceSetups.AboutToCheckMateSetup
import com.krihl4n.api.pieceSetups.AboutToStalemateSetup
import com.krihl4n.api.pieceSetups.CastlingPieceSetup
import com.krihl4n.api.pieceSetups.EnPassantSetup
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.api.pieceSetups.WhitePawnPromotionSetup
import com.krihl4n.api.pieceSetups.SimpleAttackSetup
import spock.lang.Specification

class GameEventsListenerTest extends Specification {

    public static final String WHITE_PLAYER_WON = "white_player_won"
    public static final String CHECK_MATE = "check_mate"
    public static final String BLACK_PLAYER_WON = "black_player_won"
    public static final String DRAW = "draw"
    public static final String STALEMATE = "stalemate"
    public static final String DEAD_POSITION = "dead_position"
    public static final String INSUFFICIENT_MATERIAL = "insufficient_material"
    public static final String PLAYER_RESIGNED = "player_resigned"
    public static final String TEST_MODE = "test_mode"
    public static final String PLAYER_ID = "player"

    GameEventListener listener = Mock(GameEventListener)
    GameEventListener secondListener = Mock(GameEventListener)

    String GAME_ID = "game-id"
    String SECOND_GAME_ID = "second-game-id"

    void setup() {

    }

    def initGame(PieceSetup setup) {
        return initGame(GAME_ID, TEST_MODE, setup, listener)
    }

    def initGame(String id = GAME_ID, String mode = TEST_MODE, PieceSetup setup = null, GameEventListener listener = listener) {
        def game = new GameOfChess(id, mode, setup)
        game.registerGameEventListener(listener)
        game.initialize()
        game.playerReady(PLAYER_ID, null)
        return game
    }

    def "should notify about basic move"() {
        given:
        def game = initGame()

        when:
        game.move(new MoveDto(PLAYER_ID, "a2", "a3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("a2", "a3")
            update.turn == "white"
        }
    }

    def "having two games, only one is notified about moving piece"() {
        given:
        def g1 = initGame(GAME_ID, TEST_MODE, null, listener)
        def g2 = initGame(SECOND_GAME_ID, TEST_MODE, null, secondListener)

        when:
        g2.move(new MoveDto(PLAYER_ID, "a2", "a3", null))

        then:
        1 * secondListener.piecePositionUpdate(SECOND_GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("a2", "a3")
            update.turn == "white"
        }
        0 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("a2", "a3")
            update.turn == "white"
        }
    }

    def "should notify about two moves when castling"() {
        given:
        def game = initGame(new CastlingPieceSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "e1", "g1", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("e1", "g1")
            update.getSecondaryMove() == new PerformedMoveDto("h1", "f1")
        }
    }

    def "should notify about attacks"() {
        given:
        def game = initGame(new SimpleAttackSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "c2", "d3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("c2", "d3")
            update.getPieceCapture() == new PieceCaptureDto("d3", new PieceDto("black", "pawn"))
        }
    }

    def "should notify when undoing basic move"() {
        given:
        def game = initGame()

        and:
        game.move(new MoveDto(PLAYER_ID, "a2", "a3", null))

        when:
        game.undoMove(PLAYER_ID)

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("a2", "a3")
            update.getReverted()
        }
    }

    def "should notify about en passant capture"() {
        given:
        def game = initGame(new EnPassantSetup())

        and:
        game.move(new MoveDto(PLAYER_ID, "d2", "d4", null))

        when:
        game.move(new MoveDto(PLAYER_ID, "e4", "d3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("e4", "d3")
            update.getPieceCapture() == new PieceCaptureDto("d4", new PieceDto("white", "pawn"))
        }
    }

    def "should notify about pawn promotion"() {
        given:
        def game = initGame(new WhitePawnPromotionSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "d7", "d8", pawnPromotion))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("d7", "d8")
            update.getPawnPromotion() == pawnPromotion
        }

        where:
        pawnPromotion << ["queen", "knight", "bishop", "rook"]
    }

    def "should notify about pawn to queen promotion and attack"() {
        given:
        def game = initGame(new WhitePawnPromotionSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "d7", "e8", "queen"))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("d7", "e8")
            update.getPieceCapture() ==  new PieceCaptureDto("e8", new PieceDto("black", "knight"))
            update.getPawnPromotion() == "queen"
        }
    }

    def "should notify about game end after check mate by white player"() {
        given:
        def game = initGame(new AboutToCheckMateSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "h2", "h1", null))

        then:
        1 * listener.gameFinished(GAME_ID, new GameResultDto(WHITE_PLAYER_WON, CHECK_MATE))
    }

    def "should notify about game end after check mate by black player"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white king",
                        "g2 black rook",
                        "h2 black rook"
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "h2", "h1", null))

        then:
        1 * listener.gameFinished(GAME_ID, new GameResultDto(BLACK_PLAYER_WON, CHECK_MATE))
    }

    def "should notify about draw due to after stalemate"() {
        given:
        def game = initGame(new AboutToStalemateSetup())

        when:
        game.move(new MoveDto(PLAYER_ID, "f6", "f7", null))

        then:
        1 * listener.gameFinished(GAME_ID, new GameResultDto(DRAW, STALEMATE))
    }

    def "should notify about draw due to insufficient material"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white king",
                        "g2 black king",
                        "a2 black bishop"
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "a1", "a2", null))

        then:
        1 * listener.gameFinished(GAME_ID, new GameResultDto(DRAW, INSUFFICIENT_MATERIAL))
    }

    def "should notify about loss after player resigned"() {
        given:
        def game = initGame()

        when:
        game.resign(PLAYER_ID)

        then:
        1 * listener.gameFinished(GAME_ID, new GameResultDto(BLACK_PLAYER_WON, PLAYER_RESIGNED))
    }

    def "should notify correctly about en passant attack"() {
        given:
        def game = initGame( new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "e5 white pawn",
                        "d7 black pawn"
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "d7", "d5", null))
        game.move(new MoveDto(PLAYER_ID, "e5", "d6", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("d7", "d5")
            update.getPieceCapture() == null
        }

        1 * listener.piecePositionUpdate(GAME_ID, _) >> { args ->
            PiecePositionUpdateDto update = args[1]
            update.getPrimaryMove() == new PerformedMoveDto("e5", "d6")
            update.getPieceCapture() == new PieceCaptureDto("d5", new PieceDto("black", "pawn"))
        }
    }
}
