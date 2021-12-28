package com.krihl4n

class CaptureTrackerSpec extends BaseSpec {

    CaptureTracker tracker

    void setup() {
        tracker = new CaptureTracker()
    }

    def "should throw exception when nothing to undo"() {
        when:
        tracker.popLastPieceCapturedAtField(aField("a1"))

        then:
        thrown(NoSuchElementException)
    }

    def "should pop captured pieces in proper order" () {
        given:
        def field = aField("a1")
        tracker.pieceCaptured(aWhiteRook(), field)
        tracker.pieceCaptured(aWhiteBishop(), field)

        when:
        def capturedPiece1 = tracker.popLastPieceCapturedAtField(field)
        def capturedPiece2 = tracker.popLastPieceCapturedAtField(field)

        then:
        capturedPiece1 == aWhiteBishop()
        capturedPiece2 == aWhiteRook()
    }
}
