package com.krihl4n.command

import com.krihl4n.BaseSpec
import spock.lang.Subject

class CommandFactorySpec extends BaseSpec {

    @Subject
    def commandFactory = new CommandFactory()

    def 'should return basic move command'() {
        when:
        def command = commandFactory.getCommand(move(aWhitePawn(), "a1 a2"))

        then:
        command instanceof BasicMoveCommand
    }
}