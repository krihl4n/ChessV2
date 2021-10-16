package com.krihl4n.command

import com.krihl4n.BaseSpec
import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class AttackMoveCommandSpec extends BaseSpec {

    PositionTracker positionTracker
    CaptureTracker captureTracker

    void setup() {
        positionTracker = new PositionTracker()
        captureTracker = new CaptureTracker()
    }

    def "should perform attack"() {
        given:
        positionTracker.setPieceAtField(aWhiteRook(), aField("c3"))
        positionTracker.setPieceAtField(aBlackPawn(), aField("c5"))
        and:
        def command = new AttackMoveCommand(new Move(aWhiteRook(), aField("c3"), aField("c5")), positionTracker, captureTracker)

        when:
        command.execute()

        then:
        positionTracker.isFieldEmpty(aField("c3"))
        positionTracker.getPieceAt(aField("c5")) == aWhiteRook()
    }

    def "should undo attack"() {
        given:
        positionTracker.setPieceAtField(aWhiteRook(), aField("c3"))
        positionTracker.setPieceAtField(aBlackPawn(), aField("c5"))
        and:
        def command = new AttackMoveCommand(new Move(aWhiteRook(), aField("c3"), aField("c5")), positionTracker, captureTracker)

        when:
        command.execute()
        and:
        command.undo()

        then:
        positionTracker.getPieceAt(aField("c3")) == aWhiteRook()
        positionTracker.getPieceAt(aField("c5")) == aBlackPawn()
    }
}
