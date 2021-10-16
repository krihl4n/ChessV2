package com.krihl4n.command

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import spock.lang.Subject
import spock.lang.Unroll

class CommandFactorySpec extends BaseSpec {

    PositionTracker positionTracker = new PositionTracker()
    @Subject
    def commandFactory = new CommandFactory(positionTracker)

    def 'should return basic move command'() {
        when:
        def command = commandFactory.getCommand(move(aWhitePawn(), "a1 a2"))

        then:
        command instanceof BasicMoveCommand
    }

    @Unroll
    def 'should not create any move command if field occupied by same color piece'() {
        given:
        positionTracker.setPieceAtField(friendlyPiece, aField("a2"))

        when:
        commandFactory.getCommand(move(movingPiece, "a1 a2"))

        then:
        thrown(IllegalArgumentException)

        where:
        movingPiece  | friendlyPiece
        aWhitePawn() | aWhitePawn()
        aBlackPawn() | aBlackPawn()
    }
}
