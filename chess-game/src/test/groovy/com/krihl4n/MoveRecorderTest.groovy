package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import com.krihl4n.api.pieceSetups.*
import spock.lang.Specification

class MoveRecorderTest extends Specification {

    public static final String TEST_MODE = "test_mode"
    public static final String PLAYER_ID = "player"

    GameEventListener listener = Mock(GameEventListener)

    String GAME_ID = "game-id"

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

    def "should notify about basic pawn move"() {
        given:
        def game = initGame()

        when:
        game.move(new MoveDto(PLAYER_ID, "a2", "a3", null))
        game.move(new MoveDto(PLAYER_ID, "c2", "c4", null))
        game.move(new MoveDto(PLAYER_ID, "b2", "b3", null))
        game.move(new MoveDto(PLAYER_ID, "a7", "a6", null))
        game.move(new MoveDto(PLAYER_ID, "b7", "b5", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("a3", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("c4", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("b3", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("a6", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("b5", it)
        }
    }

    def "reverted move should has the same label"() {
        given:
        def game = initGame()

        when:
        game.move(new MoveDto(PLAYER_ID, "a2", "a3", null))
        game.undoMove(PLAYER_ID)

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            PiecePositionUpdateDto update = it[1]
            update.getRecordedMove() == "a3"
            !update.getReverted()
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            PiecePositionUpdateDto update = it[1]
            update.getRecordedMove() == "a3"
            update.getReverted()
        }
    }

    def "should notify about basic figure moves"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white bishop",
                        "b1 white knight",
                        "c1 white rook",
                        "d1 white queen",
                        "e1 white king",
                        "a8 black bishop",
                        "b8 black knight",
                        "c8 black rook",
                        "d8 black queen",
                        "e8 black king",
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "a1", "b2", null))
        game.move(new MoveDto(PLAYER_ID, "b1", "a3", null))
        game.move(new MoveDto(PLAYER_ID, "c1", "c2", null))
        game.move(new MoveDto(PLAYER_ID, "d1", "d2", null))
        game.move(new MoveDto(PLAYER_ID, "e1", "e2", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Bb2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Na3", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rc2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Qd2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Ke2", it)
        }
    }

    def "should notify about basic figure attacks"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white bishop",
                        "b1 white knight",
                        "c1 white rook",
                        "d1 white queen",
                        "f1 white king",
                        "a8 black bishop",
                        "b8 black knight",
                        "c8 black rook",
                        "d8 black queen",
                        "e8 black king",
                        "b2 black pawn",
                        "a3 black pawn",
                        "c2 black pawn",
                        "d2 black pawn",
                        "f2 black pawn",
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "a1", "b2", null))
        game.move(new MoveDto(PLAYER_ID, "b1", "a3", null))
        game.move(new MoveDto(PLAYER_ID, "c1", "c2", null))
        game.move(new MoveDto(PLAYER_ID, "d1", "d2", null))
        game.move(new MoveDto(PLAYER_ID, "f1", "f2", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Bxb2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Nxa3", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rxc2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Qxd2", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Kxf2", it)
        }
    }

    def "pawn attack"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a2 white pawn",
                        "b3 black pawn"
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "a2", "b3", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("axb3", it)
        }
    }

    def "en passant attack"() {
        given:
        def game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a5 white pawn",
                        "b7 black pawn",
                        "b8 black queen",
                        "c8 black king",
                        "b1 white queen",
                        "c1 white king"
                ]
            }
        })

        when:
        game.move(new MoveDto(PLAYER_ID, "b7", "b5", null))
        game.move(new MoveDto(PLAYER_ID, "a5", "b6", null))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("b5", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("axb6", it)
        }
    }

    // https://en.wikipedia.org/wiki/Algebraic_notation_(chess)#Disambiguating_moves

    private void moveIs(move, args) {
        PiecePositionUpdateDto update = args[1]
        update.getRecordedMove() == move
    }
}
