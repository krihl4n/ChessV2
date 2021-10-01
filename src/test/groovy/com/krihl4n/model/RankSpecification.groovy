package com.krihl4n.model

import com.krihl4n.model.Rank
import spock.lang.Specification

class RankSpecification extends Specification {

    def "should create a rank object with #token"() {
        when:
        def rank = new Rank(token)

        then:
        rank.token == token

        where:
        token << ['1', '2', '3', '4', '5', '6', '7', '8']
    }

    def "should not allow wrong token #token"() {
        when:
        new Rank(token)

        then:
        thrown(IllegalArgumentException)

        where:
        token << ['0', '-1', '9', ' ', '22']
    }
}
