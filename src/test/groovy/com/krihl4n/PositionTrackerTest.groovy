package com.krihl4n

import spock.lang.Specification

class PositionTrackerTest extends Specification {

    def positionTracker

    void setup() {
        positionTracker = new PositionTracker()
    }

    def "should return null when there is no piece at position" () {
        when:
        def retrievedPiece = positionTracker.getPieceAt(new Field("a1"))

        then:
        retrievedPiece == null
    }

    def "when piece is set on a position, it can later be retrieved from that position"() {
        given:
        Piece piece = new Piece(Color.WHITE, Type.PAWN)
        Field field = new Field("a2")
        positionTracker.set(piece, field)

        when:
        def retrievedPiece = positionTracker.getPieceAt(field)

        then:
        retrievedPiece == piece
    }
}
