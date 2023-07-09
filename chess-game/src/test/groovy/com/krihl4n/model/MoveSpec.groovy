package com.krihl4n.model

import com.krihl4n.BaseSpec

class MoveSpec extends BaseSpec {

    def "move has to be performed to different field"() {
        given:
        def field = new Field("a1")

        when:
        new Move(aWhitePawn(), field, field, false, null)

        then:
        thrown(IllegalArgumentException)
    }
}
