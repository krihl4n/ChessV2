package com.krihl4n

import com.krihl4n.model.Field
import com.krihl4n.model.Piece

class PositionTrackerSpec extends BaseSpec {

    PositionTracker positionTracker

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
        Piece piece = aWhitePawn()
        Field field = new Field("a2")
        positionTracker.setPieceAtField(piece, field)

        when:
        def retrievedPiece = positionTracker.getPieceAt(field)

        then:
        retrievedPiece == piece
    }

    def "when move is performed, piece is moved from one field to another"() {
        given:
        Piece piece = aWhitePawn()
        Field start = new Field("a2")
        Field destination = new Field("a3")
        positionTracker.setPieceAtField(piece, start)

        when:
        positionTracker.movePiece(start, destination)

        then:
        positionTracker.getPieceAt(start) == null
        positionTracker.getPieceAt(destination) == piece
    }

    def "throw exception if no piece at start field"() {
        when:
        positionTracker.movePiece(new Field("a1"), new Field("a2"))

        then:
        thrown(IllegalArgumentException)
    }

    def "throw exception if start and destination fields are the same"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), new Field("a1"))

        when:
        positionTracker.movePiece(new Field("a1"), new Field("a1"))

        then:
        thrown(IllegalArgumentException)
    }

    def "should be able to remove piece from field"() {
        given:
        Field field = new Field("a1")
        positionTracker.setPieceAtField(aWhitePawn(), field)

        when:
        positionTracker.removePieceFromField(field)

        then:
        positionTracker.getPieceAt(field) == null
    }

    def "should indicate if field is empty"() {
        given:
        positionTracker.removePieceFromField(aField("a1"))

        when:
        def result = positionTracker.isFieldEmpty(aField("a1"))

        then:
        result
    }

    def "should indicate if field is not empty"() {
        given:
        positionTracker.setPieceAtField(aWhitePawn(), aField("a1"))

        when:
        def result = positionTracker.isFieldEmpty(aField("a1"))

        then:
        !result
    }
}
