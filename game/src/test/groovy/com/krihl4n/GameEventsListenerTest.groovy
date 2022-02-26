package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.GameResultDto
import com.krihl4n.api.dto.GameStateUpdateDto
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.dto.PieceCaptureDto
import com.krihl4n.api.dto.PieceDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.pieceSetups.AboutToCheckMateSetup
import com.krihl4n.api.pieceSetups.AboutToStalemateSetup
import com.krihl4n.api.pieceSetups.CastlingPieceSetup
import com.krihl4n.api.pieceSetups.EnPassantSetup
import com.krihl4n.api.pieceSetups.PieceSetup
import com.krihl4n.api.pieceSetups.QueenConversionSetup
import com.krihl4n.api.pieceSetups.SimpleAttackSetup
import spock.lang.Specification

import static com.krihl4n.api.dto.GameModeDto.FREE_MOVE

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
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "a2", "a3")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("a2", "a3"),
                        null,
                        null,
                        false,
                        false))
    }

    def "having two games, only one is notified about moving piece"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.start("player", FREE_MOVE)
        and:
        secondGameOfChess.setupChessboard(null)
        secondGameOfChess.start("player", FREE_MOVE)

        when:
        secondGameOfChess.move("player", "a2", "a3")

        then:
        1 * secondListener.piecePositionUpdate(SECOND_GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("a2", "a3"),
                        null,
                        null,
                        false,
                        false))
        0 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("a2", "a3"),
                        null,
                        null,
                        false,
                        false))
    }

    def "should notify about two moves when castling"() {
        given:
        gameOfChess.setupChessboard(new CastlingPieceSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "e1", "g1")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("e1", "g1"),
                        new MoveDto("h1", "f1"),
                        null,
                        false,
                        false
                )
        )
    }

    def "should notify about attacks"() {
        given:
        gameOfChess.setupChessboard(new SimpleAttackSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "c2", "d3")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("c2", "d3"),
                        null,
                        new PieceCaptureDto("d3", new PieceDto("BLACK", "PAWN")),
                        false,
                        false
                )
        )
    }

    def "should notify when undoing basic move"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.start("player", FREE_MOVE)

        and:
        gameOfChess.move("player", "a2", "a3")

        when:
        gameOfChess.undoMove()

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("a2", "a3"),
                        null,
                        null,
                        false,
                        true))
    }

    def "should send event when redoing a move"() {
        given:
        gameOfChess.setupChessboard(null)
        gameOfChess.start("player", FREE_MOVE)

        and:
        gameOfChess.move("player", "a2", "a3")
        gameOfChess.undoMove()

        when:
        gameOfChess.redoMove()

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("a2", "a3"),
                        null,
                        null,
                        false,
                        false))
    }

    def "should notify about en passant capture"() {
        given:
        gameOfChess.setupChessboard(new EnPassantSetup())
        gameOfChess.start("player", FREE_MOVE)

        and:
        gameOfChess.move("player", "d2", "d4")

        when:
        gameOfChess.move("player", "e4", "d3")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("e4", "d3"),
                        null,
                        new PieceCaptureDto("d4", new PieceDto("WHITE", "PAWN")),
                        false,
                        false
                )
        )
    }

    def "should notify about pawn to queen conversion"() {
        given:
        gameOfChess.setupChessboard(new QueenConversionSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "d7", "d8")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("d7", "d8"),
                        null,
                        null,
                        true,
                        false
                )
        )
    }

    def "should notify about pawn to queen conversion and attack"() {
        given:
        gameOfChess.setupChessboard(new QueenConversionSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "d7", "e8")

        then:
        1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("d7", "e8"),
                        null,
                        new PieceCaptureDto("e8", new PieceDto("BLACK", "KNIGHT")),
                        true,
                        false
                )
        )
    }

    def "should notify about game end after check mate by white player"() {
        given:
        gameOfChess.setupChessboard(new AboutToCheckMateSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "h2", "h1")

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
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "h2", "h1")

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("BLACK_PLAYER_WON", "CHECK_MATE"))
    }

    def "should notify about draw due to after stalemate"() {
        given:
        gameOfChess.setupChessboard(new AboutToStalemateSetup())
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "f6", "f7")

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
        gameOfChess.start("player", FREE_MOVE)

        when:
        gameOfChess.move("player", "a1", "a2")

        then:
        1 * listener.gameStateUpdate(GAME_ID, new GameStateUpdateDto("FINISHED"))
        1 * listener.gameFinished(GAME_ID, new GameResultDto("DRAW", "DEAD_POSITION"))
    }
}
