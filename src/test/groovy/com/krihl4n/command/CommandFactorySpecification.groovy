package com.krihl4n.command

import com.krihl4n.model.Field
import com.krihl4n.model.Move
import spock.lang.Specification
import spock.lang.Subject
import static com.krihl4n.examples.Pieces.aWhitePawn

class CommandFactorySpecification extends Specification {

    @Subject
    def commandFactory = new CommandFactory()

    def 'should return basic move command'() {
        when:
        def command = commandFactory
                .getCommand(new Move(aWhitePawn(), new Field("a1"), new Field("a2")))

        then:
        command instanceof BasicMoveCommand
    }
}
