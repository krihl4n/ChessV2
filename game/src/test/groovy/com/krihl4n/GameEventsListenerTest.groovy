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
import com.krihl4n.api.pieceSetups.PawnConversionSetup
import com.krihl4n.api.pieceSetups.SimpleAttackSetup
import spock.lang.Specification

import static com.krihl4n.api.dto.GameModeDto.*

class GameEventsListenerTest extends Specification {

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

    def "should notify about basic move"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "a2", "a3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("a2", "a3"),
                        null,
                        null,
                        null,
                        false,
                        "WHITE"))
    }

    def "having two games, only one is notified about moving piece"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)
        and:
        secondGameOfChess.setupChessboard(null)
        secondGameOfChess.requestNewGame(TEST_MODE)
        secondGameOfChess.playerReady("player2", null)

        when:
        secondGameOfChess.move(new MoveDto("player", "a2", "a3", null))

        then:
        1 * secondListener.piecePositionUpdate(SECOND_GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("a2", "a3"),
                        null,
                        null,
                        null,
                        false,
                        "WHITE"))
        0 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("a2", "a3"),
                        null,
                        null,
                        null,
                        false,
                        "WHITE"))
    }

    def "should notify about two moves when castling"() {
        given:
        gameOfChess.setupChessboard(new CastlingPieceSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "e1", "g1", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("e1", "g1"),
                        new PerformedMoveDto("h1", "f1"),
                        null,
                        null,
                        false,
                        "WHITE"
                )
        )
    }

    def "should notify about attacks"() {
        given:
        gameOfChess.setupChessboard(new SimpleAttackSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "c2", "d3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("c2", "d3"),
                        null,
                        new PieceCaptureDto("d3", new PieceDto("BLACK", "PAWN")),
                        null,
                        false,
                        "WHITE"
                )
        )
    }

    def "should notify when undoing basic move"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        and:
        gameOfChess.move(new MoveDto("player", "a2", "a3", null))

        when:
        gameOfChess.undoMove()

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("a2", "a3"),
                        null,
                        null,
                        null,
                        true,
                        "WHITE"))
    }

    def "should send event when redoing a move"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        and:
        gameOfChess.move(new MoveDto("player", "a2", "a3", null))
        gameOfChess.undoMove()

        when:
        gameOfChess.redoMove()

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("a2", "a3"),
                        null,
                        null,
                        null,
                        false,
                        "WHITE"))
    }

    def "should notify about en passant capture"() {
        given:
        gameOfChess.setupChessboard(new EnPassantSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        and:
        gameOfChess.move(new MoveDto("player", "d2", "d4", null))

        when:
        gameOfChess.move(new MoveDto("player", "e4", "d3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("e4", "d3"),
                        null,
                        new PieceCaptureDto("d4", new PieceDto("WHITE", "PAWN")),
                        null,
                        false,
                        "WHITE"
                )
        )
    }

    def "should notify about pawn conversion"() {
        given:
        gameOfChess.setupChessboard(new PawnConversionSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "d7", "d8", conversionReq))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("d7", "d8"),
                        null,
                        null,
                        conversion,
                        false,
                        "WHITE"
                )
        )

        where:
        conversionReq || conversion
        "queen"       || "QUEEN"
        "knight"      || "KNIGHT"
        "bishop"      || "BISHOP"
        "rook"        || "ROOK"
    }

    def "should notify about pawn to queen conversion and attack"() {
        given:
        gameOfChess.setupChessboard(new PawnConversionSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "d7", "e8", "queen"))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("d7", "e8"),
                        null,
                        new PieceCaptureDto("e8", new PieceDto("BLACK", "KNIGHT")),
                        "QUEEN",
                        false,
                        "WHITE"
                )
        )
    }

    def "should notify about game start"() {
        given:
        gameOfChess.setupChessboard(new AboutToCheckMateSetup())

        when:
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("IN_PROGRESS"))
    }

    def "should notify about waiting for players state change"() {
        given:
        gameOfChess.setupChessboard(new AboutToCheckMateSetup())

        when:
        gameOfChess.requestNewGame(VS_FRIEND)

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("WAITING_FOR_PLAYERS"))
    }

    def "should notify about game start after player two joins"() {
        given:
        gameOfChess.setupChessboard(new AboutToCheckMateSetup())
        gameOfChess.requestNewGame(VS_FRIEND)
        gameOfChess.playerReady("player1", null)

        when:
        gameOfChess.playerReady("player2", null)

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("IN_PROGRESS"))
    }

    def "should notify about game end after check mate by white player"() {
        given:
        gameOfChess.setupChessboard(new AboutToCheckMateSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "h2", "h1", null))

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("WHITE_PLAYER_WON", "CHECK_MATE"))
    }

    def "should notify about game end after check mate by black player"() {
        given:
        gameOfChess.setupChessboard(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white king",
                        "g2 black rook",
                        "h2 black rook"
                ]
            }
        })
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "h2", "h1", null))

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("BLACK_PLAYER_WON", "CHECK_MATE"))
    }

    def "should notify about draw due to after stalemate"() {
        given:
        gameOfChess.setupChessboard(new AboutToStalemateSetup())
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "f6", "f7", null))

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("DRAW", "STALEMATE"))
    }

    def "should notify about draw due to dead position"() {
        given:
        gameOfChess.setupChessboard(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white king",
                        "g2 black king",
                        "a2 black bishop"
                ]
            }
        })
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "a1", "a2", null))

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("DRAW", "DEAD_POSITION"))
    }

    def "should notify about loss after player resigned"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.resign("player")

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("BLACK_PLAYER_WON", "PLAYER_RESIGNED"))
    }

    def "should notify correctly about en passant attack"() {
        given:
        gameOfChess.setupChessboard(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "e5 white pawn",
                        "d7 black pawn"
                ]
            }
        })
        gameOfChess.requestNewGame(TEST_MODE)
        gameOfChess.playerReady("player", null)

        when:
        gameOfChess.move(new MoveDto("player", "d7", "d5", null))
        gameOfChess.move(new MoveDto("player", "e5", "d6", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new PerformedMoveDto("e5", "d6"),
                        null,
                        new PieceCaptureDto("d5", new PieceDto("BLACK", "PAWN")),
                        null,
                        false,
                        "WHITE"
                )
        )
    }
}
