package com.krihl4n

import spock.lang.Specification

class FileSpecification extends Specification {

    def "should create a file object with #token"() {
        when:
        def file = new File(token)

        then:
        file.getToken() == token

        where:
        token << ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']
    }

    def "should normalize tokens"() {
        when:
        def file = new File(token)

        then:
        file.getToken() == token.toLowerCase()

        where:
        token << ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
    }
}
