package com.krihl4n.model

import com.krihl4n.BaseSpec

class RankSpec extends BaseSpec {

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

    def "should add #number and create next rank"() {
        given:
        def rank = new Rank("1")

        when:
        def result = rank + number

        then:
        result == new Rank(expectedToken)

        where:
        number | expectedToken
        1      | "2"
        2      | "3"
        3      | "4"
        4      | "5"
        5      | "6"
        6      | "7"
        7      | "8"
    }

    def "should return null when exceeding limit"() {
        given:
        def rank = new Rank("1")

        when:
        def result = rank + 8

        then:
        result == null
    }

    def "should get previous rank"() {
        given:
        def rank = new Rank("2")

        when:
        def result = rank - 1

        then:
        result == new Rank("1")
    }
}
