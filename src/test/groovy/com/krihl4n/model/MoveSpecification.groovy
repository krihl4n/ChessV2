package com.krihl4n.model

import spock.lang.Specification

class MoveSpecification extends Specification{

    def "move has to be performed to different field"() {
        given:
        def field = new Field("a1")

        when:
        new Move(new Piece(Color.WHITE, Type.PAWN), field, field)

        then:
        thrown(IllegalArgumentException)
    }
}
