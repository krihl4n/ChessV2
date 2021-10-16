package com.krihl4n.command

import com.krihl4n.BaseSpec
import com.krihl4n.PositionTracker
import spock.lang.Subject

class CommandFactorySpec extends BaseSpec {

    PositionTracker positionTracker = new PositionTracker()
    @Subject
    def commandFactory = new CommandFactory(positionTracker)

    def 'should return basic move command'() {
        when:
        def command = commandFactory.getCommand(move(aWhitePawn(), "a2 a3"))

        then:
        command instanceof BasicMoveCommand
    }

    def "should return attack command"() {
        given:
        positionTracker.setPieceAtField(aBlackPawn(), aField("b3"))

        when:
        def command = commandFactory.getCommand(move(aWhitePawn(), "a2 b3"))

        then:
        command instanceof AttackMoveCommand
    }
}
