package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.MoveDto
import com.krihl4n.api.dto.PieceCaptureDto
import com.krihl4n.api.dto.PieceDto
import com.krihl4n.api.dto.PiecePositionUpdateDto
import com.krihl4n.api.pieceSetups.CastlingPieceSetup
import com.krihl4n.api.pieceSetups.SimpleAttackSetup
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
            gameOfChess.setupChessboard(null)
            gameOfChess.start()

        when:
            gameOfChess.move("a2", "a3")

        then:
            1 * listener.piecePositionUpdate(GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("a2", "a3"),
                            null,
                            null,
                            false))
    }

    def "having two games, only one is notified about moving piece"() {
        given:
            gameOfChess.setupChessboard(null)
            gameOfChess.start()
        and:
            secondGameOfChess.setupChessboard(null)
            secondGameOfChess.start()

        when:
            secondGameOfChess.move("a2", "a3")

        then:
            1 * secondListener.piecePositionUpdate(SECOND_GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("a2", "a3"),
                            null,
                            null,
                            false))
            0 * listener.piecePositionUpdate(GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("a2", "a3"),
                            null,
                            null,
                            false))
    }

    def "should notify about two moves when castling"() {
        given:
            gameOfChess.setupChessboard(new CastlingPieceSetup())
            gameOfChess.start()

        when:
            gameOfChess.move("e1", "g1")

        then:
            1 * listener.piecePositionUpdate(GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("e1", "g1"),
                            new MoveDto("h1", "f1"),
                            null,
                            false
                    )
            )
    }

    def "should notify about attacks"() {
        given:
            gameOfChess.setupChessboard(new SimpleAttackSetup())
            gameOfChess.start()

        when:
            gameOfChess.move("c2", "d3")

        then:
            1 * listener.piecePositionUpdate(GAME_ID,
                new PiecePositionUpdateDto(
                        new MoveDto("c2", "d3"),
                        null,
                        new PieceCaptureDto("d3", new PieceDto("BLACK", "PAWN")),
                        false
                )
            )
    }

    def "should notify when undoing basic move"() {
        given:
            gameOfChess.setupChessboard(null)
            gameOfChess.start()

        and:
            gameOfChess.move("a2", "a3")

        when:
            gameOfChess.undoMove()

        then:
            1 * listener.piecePositionUpdate(GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("a2", "a3"),
                            null,
                            null,
                            true))
    }

    def "should send event when redoing a move"() {
        given:
            gameOfChess.setupChessboard(null)
            gameOfChess.start()

        and:
            gameOfChess.move("a2", "a3")
            gameOfChess.undoMove()

        when:
            gameOfChess.redoMove()

        then:
            1 * listener.piecePositionUpdate(GAME_ID,
                    new PiecePositionUpdateDto(
                            new MoveDto("a2", "a3"),
                            null,
                            null,
                            false))
    }
}
