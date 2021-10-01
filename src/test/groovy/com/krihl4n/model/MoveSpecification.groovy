package com.krihl4n.model

import spock.lang.Specification
import static com.krihl4n.examples.Pieces.*

class MoveSpecification extends Specification{

    def "move has to be performed to different field"() {
        given:
        def field = new Field("a1")

        when:
        new Move(aWhitePawn(), field, field)

        then:
        thrown(IllegalArgumentException)
    }
}
