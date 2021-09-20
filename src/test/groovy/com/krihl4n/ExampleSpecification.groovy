package com.krihl4n

import spock.lang.Specification

class ExampleSpecification extends Specification {

    def "should be a simple assertion"() {
        expect:
        MainKt.dummyFun()
    }
}
