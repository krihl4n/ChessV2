package com.krihl4n.model

import com.krihl4n.BaseSpec

class FileSpec extends BaseSpec {

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
        file.token == token.toLowerCase()

        where:
        token << ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H']
    }

    def "should not allow wrong token #token"() {
        when:
        new File(token)

        then:
        thrown(IllegalArgumentException)

        where:
        token << ['w', 'O', '?', ' ', 'aa', 'AA']
    }
}
