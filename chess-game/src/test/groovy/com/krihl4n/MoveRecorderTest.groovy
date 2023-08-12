package com.krihl4n

import com.krihl4n.api.GameEventListener
import com.krihl4n.api.GameOfChess
import com.krihl4n.api.dto.*
import com.krihl4n.api.pieceSetups.*
import spock.lang.Specification

class MoveRecorderTest extends Specification {

    public static final String TEST_MODE = "test_mode"
    public static final String PLAYER_ID = "player"

    private GameEventListener listener = Mock(GameEventListener)

    private String GAME_ID = "game-id"
    private GameOfChess game

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
        this.game = initGame()

        when:
        move( "a2", "a3")
        move( "c2", "c4")
        move( "b2", "b3")
        move( "a7", "a6")
        move( "b7", "b5")

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
        this.game = initGame()

        when:
        move("a2", "a3")
        game.undoMove(PLAYER_ID)

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            PiecePositionUpdateDto update = it[1]
            assert update.getLabel() == "a3"
            assert !update.getReverted()
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            PiecePositionUpdateDto update = it[1]
            assert  update.getLabel() == "a3"
            assert  update.getReverted()
        }
    }

    def "should notify about basic figure moves"() {
        given:
        this.game = initGame(new PieceSetup() {
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
        move("a1", "b2")
        move("b1", "a3")
        move("c1", "c2")
        move("d1", "d2")
        move("e1", "e2")

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
        this.game = initGame(new PieceSetup() {
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
        move("a1", "b2")
        move("b1", "a3")
        move("c1", "c2")
        move("d1", "d2")
        move("f1", "f2")

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
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a2 white pawn",
                        "b3 black pawn"
                ]
            }
        })

        when:
        move("a2", "b3")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("axb3", it)
        }
    }

    def "en passant attack"() {
        given:
        this.game = initGame(new PieceSetup() {
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
        move("b7", "b5")
        move("a5", "b6")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("b5", it)
        }
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("axb6", it)
        }
    }

    def "king side castling"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "h1 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                ]
            }
        })

        when:
        move("e1", "g1")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("O-O", it)
        }
    }

    def "queen side castling"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "h1 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                ]
            }
        })

        when:
        move("e1", "c1")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("O-O-O", it)
        }
    }

    def "pawn promotion"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "h1 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                        "b7 white pawn"
                ]
            }
        })

        when:
        this.game.move(new MoveDto(PLAYER_ID, "b7", "b8", pawnPromotion))

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs(movelabel, it)
        }

        where:
        pawnPromotion || movelabel
        "queen"       || "b8Q+"
        "rook"        || "b8R+"
        "knight"      || "b8N"
        "bishop"      || "b8B"
    }

    def "check"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "h1 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                ]
            }
        })

        when:
        move("a1", "a8")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rxa8+", it)
        }
    }

    def "ambiguous move - label extended with rank of departure"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "a3 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                ]
            }
        })

        when:
        move("a1", "a2")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("R1a2", it)
        }
    }

    def "ambiguous move - label extended with file of departure"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a3 white rook",
                        "c3 white rook",
                        "e1 white king",
                        "a8 black rook",
                        "h8 black rook",
                        "e8 black king",
                ]
            }
        })

        when:
        move("a3", "b3")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rab3", it)
        }
    }

    def "ambiguous move - label extended with field of departure"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "h1 white queen",
                        "h4 white queen",
                        "e4 white queen",
                        "a1 white king",
                        "b7 black queen",
                        "a8 black king",
                ]
            }
        })

        when:
        move("h4", "e1")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Qh4e1", it)
        }
    }

    def "ambiguous move - rook test 1"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "b1 white rook",
                        "g1 white king",
                        "h2 black rook",
                        "a8 black rook",
                        "d7 black king",
                ]
            }
        })

        when:
        move("a8", "h8")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rah8", it)
        }
    }

    def "ambiguous move - rook test 2"() {
        given:
        this.game = initGame(new PieceSetup() {
            @Override
            List<String> get() {
                return [
                        "a1 white rook",
                        "b1 white rook",
                        "g1 white king",
                        "h2 black rook",
                        "a8 black rook",
                        "d7 black king",
                ]
            }
        })

        when:
        move("h2", "h8")

        then:
        1 * listener.piecePositionUpdate(GAME_ID, _) >> {
            moveIs("Rhh8", it)
        }
    }

// https://en.wikipedia.org/wiki/Algebraic_notation_(chess)#Disambiguating_moves

    private void move(String from, String to) {
        this.game.move(new MoveDto(PLAYER_ID, from, to, null))
    }

    private void moveIs(move, args) {
        PiecePositionUpdateDto update = args[1]
        assert update.getLabel() == move
    }
}
