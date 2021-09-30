package com.krihl4n

import spock.lang.Specification

class PositionTrackerTest extends Specification {

    def positionTracker

    void setup() {
        positionTracker = new PositionTracker()
    }

    def "should return null when there is no piece at position"() {
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

    def "when move is performed, piece is moved from one field to another"() {
        given:
        Piece piece = new Piece(Color.WHITE, Type.PAWN)
        Field start = new Field("a2")
        Field destination = new Field("a3")
        positionTracker.set(piece, start)

        when:
        positionTracker.move(start, destination)

        then:
        positionTracker.getPieceAt(start) == null
        positionTracker.getPieceAt(destination) == piece
    }

    def "throw exception if no piece at start field"() {
        when:
        positionTracker.move(new Field("a1"), new Field("a2"))

        then:
        thrown(IllegalArgumentException)
    }

    def "throw exception if start and destination fields are the same"() {
        given:
        positionTracker.set(new Piece(Color.WHITE, Type.PAWN), new Field("a1"))

        when:
        positionTracker.move(new Field("a1"), new Field("a1"))

        then:
        thrown(IllegalArgumentException)
    }
}
