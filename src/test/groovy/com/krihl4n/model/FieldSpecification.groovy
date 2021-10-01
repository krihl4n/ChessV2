package com.krihl4n.model

import spock.lang.Specification

class FieldSpecification extends Specification {

    def "field object can be created giving string token" () {
        when:
        Field field = new Field("a1")

        then:
        field == new Field(new File("a"), new Rank("1"))
    }

    def "field should be created with two character tokens"() {
        when:
        new Field("a11")

        then:
        thrown(IllegalArgumentException)
    }
}
