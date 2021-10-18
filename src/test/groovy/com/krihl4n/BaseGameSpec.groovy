package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class BaseGameSpec extends BaseSpec {

    static def assertPositions(String expression, PositionTracker positionTracker) {
        def map = getPiecePositionsMap(expression)
        def expected = toStringChessboard(map)
        def actual = toStringChessboard(getPositionsFromTracker(positionTracker.positionsOfAllPieces))
        assert expected == actual
        return this
    }

    private static def getPositionsFromTracker(Map<Field, Piece> mapFromTracker) {
        def map = [:]
        mapFromTracker.each { k, v ->
            def stringVal = k.toString()
            def stripped = stringVal.substring(stringVal.indexOf('(') + 1, stringVal.indexOf(')'))
            map.put(stripped, determineToken(v.color) + determineToken(v.type))
        }
        return map
    }

    private static def getPiecePositionsMap(String expression) {
        String[] tokens = expression.split("[\\n\\r\\s]+")

        def map = [:]
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
        if (piece == null) return "  " else return piece.trim()
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

    private static def determineToken(Color pieceColor) {
        switch (pieceColor) {
            case Color.WHITE: return "w"
            case Color.BLACK: return "b"
        }
    }
}
