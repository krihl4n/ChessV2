package com.krihl4n

import com.krihl4n.guards.CastlingGuard
import com.krihl4n.guards.CheckGuard
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import com.krihl4n.model.Type
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.moveCommands.CommandFactory
import spock.lang.Subject

class BaseGameSpec extends BaseSpec {

    @Subject
    Game game
    PositionTracker positionTracker

    void setup() {
        CalculatorFactory calculatorFactory = new CalculatorFactory()
        positionTracker = new PositionTracker()
        CastlingGuard castlingGuard = new CastlingGuard(positionTracker, calculatorFactory)
        CheckGuard checkGuard = new CheckGuard(positionTracker)
        CommandCoordinator commandCoordinator = new CommandCoordinator()
        commandCoordinator.registerObserver(castlingGuard)
        MoveValidator moveValidator = new MoveValidator(
                new PieceMoveCalculator(positionTracker, calculatorFactory),
                checkGuard
        )
        CommandFactory commandFactory = new CommandFactory(positionTracker)
        game = new Game(moveValidator, commandCoordinator, commandFactory, positionTracker)
        EnPassantGuard enPassantGuard = new EnPassantGuard(positionTracker, commandCoordinator)
        calculatorFactory.initCalculators(enPassantGuard, castlingGuard)
    }

    def performMove(String move) {
        performMove("player", move)
    }

    def performMove(String player, String move) {
        String[] tokens = move.split(' ')
        game.performMove(player, tokens[0], tokens[1])
    }

    def undoMove() {
        game.undoMove()
    }

    def setupPieces(String expression) {
        Map<String, String> map = getPiecePositionsMap(expression)
        for (item in map) {
            Field field = aField(item.key)
            Piece piece = new Piece(determineColor(item.value[0]), determineType(item.value[1]))
            positionTracker.setPieceAtField(piece, field)
        }
    }

    def assertPositions(String expression) {
        def map = getPiecePositionsMap(expression)
        def expected = toStringChessboard(map)
        def actual = toStringChessboard(getPositionsFromTracker(positionTracker.positionsOfAllPieces))
        assert actual == expected
        return this
    }

    def assertSinglePosition(Piece piece, String field) {
        positionTracker.getPieceAt(new Field(field)) == piece
    }

    private static Map<String, String> getPositionsFromTracker(Map<Field, Piece> mapFromTracker) {
        Map<String, String> map = [:]
        mapFromTracker.each { k, v ->
            def stringVal = k.toString()
            def stripped = stringVal.substring(stringVal.indexOf('(') + 1, stringVal.indexOf(')'))
            map.put(stripped, determineToken(v.color) + determineToken(v.type))
        }
        return map
    }

    private static Map<String, String> getPiecePositionsMap(String expression) {
        String[] tokens = expression.split("[\\n\\r\\s]+")

        Map<String, String> map = [:]
        tokens.each { token ->
            token = token.toLowerCase()
            map.put(token[3] + token[4], token[0] + token[1])
        }
        return map
    }

    private static def toStringChessboard(def piecePositions) {
        String chessboard = ""

        chessboard += "    ---- ---- ---- ---- ---- ---- ---- ----\n"

        for (int i = 8; i > 0; i--) {
            chessboard +=
                    i + "  | " + getPiece("a" + i, piecePositions) + " |" +
                            " " + getPiece("b" + i, piecePositions) + " |" +
                            " " + getPiece("c" + i, piecePositions) + " |" +
                            " " + getPiece("d" + i, piecePositions) + " |" +
                            " " + getPiece("e" + i, piecePositions) + " |" +
                            " " + getPiece("f" + i, piecePositions) + " |" +
                            " " + getPiece("g" + i, piecePositions) + " |" +
                            " " + getPiece("h" + i, piecePositions) + " |" +
                            "\n"
            chessboard += "    ---- ---- ---- ---- ---- ---- ---- ----\n"
        }

        chessboard += "     a    b    c    d    e    f    g    h"
        return chessboard
    }

    private static getPiece(String field, def piecePositions) {
        def piece = piecePositions[field]
        if (piece == null) return "  " else return piece
    }

    private static def determineToken(Type pieceType) {
        switch (pieceType) {
            case Type.PAWN: return "p"
            case Type.ROOK: return "r"
            case Type.KNIGHT: return "n"
            case Type.BISHOP: return "b"
            case Type.QUEEN: return "q"
            case Type.KING: return "k"
        }
    }

    private static def determineType(String token) {
        switch (token) {
            case "p": return Type.PAWN
            case "r": return Type.ROOK
            case "n": return Type.KNIGHT
            case "b": return Type.BISHOP
            case "q": return Type.QUEEN
            case "k": return Type.KING
        }
    }

    private static def determineToken(Color pieceColor) {
        switch (pieceColor) {
            case Color.WHITE: return "w"
            case Color.BLACK: return "b"
        }
    }

    private static Color determineColor(String token) {
        switch (token) {
            case "w": return Color.@WHITE
            case "b": return Color.@BLACK
        }
        throw new IllegalArgumentException("cannot determine color")
    }
}
