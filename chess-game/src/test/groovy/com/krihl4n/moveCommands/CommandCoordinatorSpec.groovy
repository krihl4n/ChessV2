package com.krihl4n.moveCommands

import com.krihl4n.BaseSpec
import spock.lang.Subject

class CommandCoordinatorSpec extends BaseSpec {

    @Subject
    def coordinator = new CommandCoordinator()

    def command = Mock(MoveCommand)

    def "should execute command"() {
        when:
        coordinator.execute(command)

        then:
        1 * command.execute()
    }

    def "should undo command"() {
        given:
        coordinator.execute(command)

        when:
        coordinator.undo()

        then:
        1 * command.undo()
    }

    def "should commands in proper order"() {
        given:
        def c1 = Mock(MoveCommand)
        def c2 = Mock(MoveCommand)

        and:
        coordinator.execute(c1)
        coordinator.execute(c2)

        when:
        coordinator.undo()

        then:
        0 * c1.undo()
        1 * c2.undo()
    }

    def "should undo two commands"() {
        given:
        def c1 = Mock(MoveCommand)
        def c2 = Mock(MoveCommand)

        and:
        coordinator.execute(c1)
        coordinator.execute(c2)

        when:
        coordinator.undo()
        coordinator.undo()

        then:
        1 * c2.undo()
        1 * c1.undo()
    }

    def "should do nothing if there is nothing to undo"() {
        given:
        coordinator.execute(command)
        coordinator.undo()

        when:
        coordinator.undo()

        then:
        0 * command.undo()
    }
}
