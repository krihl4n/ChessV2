package com.krihl4n.command

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.Type
import spock.lang.Specification
import spock.lang.Subject

class CommandFactorySpecification extends Specification {

    @Subject
    def commandFactory = new CommandFactory()

    def 'should return basic move command'() {
        when:
        def command = commandFactory
                .getCommand(new Move(new Piece(Color.WHITE, Type.PAWN), new Field("a1"), new Field("a2")))

        then:
        command instanceof BasicMoveCommand
    }
}
