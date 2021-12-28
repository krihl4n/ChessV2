package com.krihl4n.model

import com.krihl4n.BaseSpec

class MoveSpec extends BaseSpec {

    def "move has to be performed to different field"() {
        given:
        def field = new Field("a1")

        when:
        new Move(aWhitePawn(), field, field)

        then:
        thrown(IllegalArgumentException)
    }

    def "create move using expression"() {
        given:

        when:
        def result = Move.of(aWhitePawn(), "a1 a2")

        then:
        result.from == new Field("a1")
        result.to == new Field("a2")
        result.piece == aWhitePawn()
    }
}
